package ro.traffic.project.impl;

import ro.traffic.project.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class VehicleOwnersProcessorImpl implements VehicleOwnersProcessor {
    private static final String DELIMITER = "\r\n";
    private static final Integer INVALID_LINE = 0;
    private static final Integer INVALID_IDENTITY_CARD = 1;
    private static final Integer INVALID_DATE = 2;
    private static final String SEMICOLON = ";";
    private static final Map<String, Integer> foreignCarsPerJud = new HashMap<>();
    private static final Set<VehicleOwnerParseError> errors = new HashSet<>();
    private static final int VALID_LINE_LENGTH = 3;

    private static double oddNumber = 0;
    private static double evenNumber = 0;
    private static Integer differentMatriculation = 0;
    private final RoIdCardParser roIdCardParser;
    private final RoRegPlateParser roRegPlateParser;
    private final Date referenceDate;

    public VehicleOwnersProcessorImpl(RoIdCardParser roIdCardParser, RoRegPlateParser roRegPlateParser, Date
            referenceDate) {
        this.roIdCardParser = roIdCardParser;
        this.roRegPlateParser = roRegPlateParser;
        this.referenceDate = referenceDate;
    }

    @Override
    public void process(InputStream ciCarRegNbInputStream, OutputStream processResultOutputStream) throws IOException {
        getMunicipalRegionalList();
        String content = getContent(ciCarRegNbInputStream);
        List<String> splitLines = getSplitLines(content);

        int lineCounter = 1;
        for (String line : splitLines) {
            List<String> splitWords = List.of(line.split(SEMICOLON));
            String municipalRegion = null;
            if (splitWords.size() >= VALID_LINE_LENGTH) {
                try {
                    municipalRegion = getMunicipalRegion(splitWords);
                } catch (InvalidRoIdCardException e) {
                    Integer ciErrorLine = lineCounter;
                    addErrorsLinesAndTypes(ciErrorLine, INVALID_IDENTITY_CARD);
                }
                Date date = getDate(lineCounter, splitWords);
                String municipalMatriculation = getMunicipalMatriculation(splitWords, municipalRegion);
                if (municipalMatriculation != null && municipalRegion != null &
                        !municipalMatriculation.equals(municipalRegion) && date != null) {
                    LocalDate localReferenceDate = getLocalReferenceDate(referenceDate);
                    LocalDate localDate = getLocalReferenceDate(date);
                    LocalDate referenceDateMinus30Days = localReferenceDate.minusDays(30);
                    if (localDate.isBefore(referenceDateMinus30Days))
                        differentMatriculation++;
                }
            } else {
                Integer lineError = lineCounter;
                addErrorsLinesAndTypes(lineError, INVALID_LINE);
            }
            lineCounter++;
        }
        transformToSerialisedStream(processResultOutputStream);
    }

    private List<String> getSplitLines(String content) {
        List<String> splitLines = Arrays.stream(content.split(DELIMITER)).collect(Collectors.toList());
        return splitLines;
    }

    private LocalDate getLocalReferenceDate(Date referenceDate) {
        return referenceDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void addErrorsLinesAndTypes(Integer ciErrorLine, Integer invalidIdentityCard) {
        errors.add(new VehicleOwnerParseError() {
            @Override
            public Integer getLine() {
                return ciErrorLine;
            }

            @Override
            public Integer getType() {
                return invalidIdentityCard;
            }
        });
    }

    private String getMunicipalRegion(List<String> splitWords) throws InvalidRoIdCardException {
        String municipalRegion;
        RoIdCardProperties idCardProperties = roIdCardParser.parseIdCard(
                splitWords.get(0));
        municipalRegion = idCardProperties.getJudet().toString();
        return municipalRegion;
    }

    private String getContent(InputStream ciCarRegNbInputStream) {
        String content = new BufferedReader(
                new InputStreamReader(ciCarRegNbInputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(DELIMITER));
        return content;
    }

    private String getMunicipalMatriculation(List<String> splitWords, String municipalRegion) {
        String municipalMatriculation = null;
        try {
            RoRegPlateProperties roRegPlateProperties = roRegPlateParser.parseRegistrationPlate(
                    splitWords.get(2));
            municipalMatriculation = roRegPlateProperties.getJudet().toString();
            if (roRegPlateProperties.getDigits() % 2 == INVALID_LINE) {
                oddNumber++;
            } else {
                evenNumber++;
            }
        } catch (InvalidRoRegPlateException e) {
            if (municipalRegion != null) {
                foreignCarsPerJud.put(municipalRegion, foreignCarsPerJud.get(municipalRegion) + 1);
            }
        }
        return municipalMatriculation;
    }

    private Date getDate(int lineCounter, List<String> splitWords) {
        Date date = null;
        try {
            date = VehicleOwnerAggregator.getFormattedDate(splitWords.get(1));
        } catch (Exception e) {
            Integer dateErrorLine = lineCounter;
            addErrorsLinesAndTypes(dateErrorLine, INVALID_DATE);
        }
        return date;
    }

    private void getMunicipalRegionalList() {
        List<String> municipalRegionsList = Arrays.stream(Judet.values()).map(Enum::toString).toList();
        municipalRegionsList.forEach(region -> foreignCarsPerJud.put(region, 0));
    }

    private void transformToSerialisedStream(OutputStream processResultOutputStream) throws IOException {
        new ObjectOutputStream(processResultOutputStream).writeObject(new VehicleOwnerProcessResult() {
            @Override
            public Integer getOddToEvenRatio() {
                return (int) (Math.ceil(oddNumber / evenNumber)) * 100;
            }

            @Override
            public Map<String, Integer> getUnregCarsCountByJud() {
                return foreignCarsPerJud;
            }

            @Override
            public Integer getPassedRegChangeDueDate() {
                return differentMatriculation;
            }

            @Override
            public Set<VehicleOwnerParseError> getErrors() {
                return errors;
            }
        });
    }
}
