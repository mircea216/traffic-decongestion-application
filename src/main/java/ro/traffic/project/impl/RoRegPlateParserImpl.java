package ro.traffic.project.impl;

import ro.traffic.project.api.InvalidRoRegPlateException;
import ro.traffic.project.api.Judet;
import ro.traffic.project.api.RoRegPlateParser;
import ro.traffic.project.api.RoRegPlateProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RoRegPlateParserImpl implements RoRegPlateParser {
    private static final String FOREIGN_CAR = "The car is foreign";
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final String BUCHAREST_ABBREVIATION = "B";
    private static final int PLATE_LENGTH = 7;

    @Override
    public RoRegPlateProperties parseRegistrationPlate(String registrationPlate) throws InvalidRoRegPlateException {
        if (registrationPlate.length() < PLATE_LENGTH)
            throw new InvalidRoRegPlateException(FOREIGN_CAR);
        String formattedRegistrationPlate = registrationPlate.trim().replaceAll(SPACE, EMPTY_STRING)
                .toUpperCase(Locale.ROOT);
        String municipalRegion = formattedRegistrationPlate.substring(0, 2);
        if (Character.isDigit(municipalRegion.charAt(1))) {
            municipalRegion = String.valueOf(municipalRegion.charAt(0));
        }
        List<String> municipalRegions = Arrays.stream(Judet.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        Short number = null;
        if (BUCHAREST_ABBREVIATION.equals(municipalRegion)) {
            try {
                number = Short.parseShort(formattedRegistrationPlate.substring(1, 4));
            } catch (NumberFormatException numberFormatException) {
                System.out.println(numberFormatException.getMessage());
            }
        } else {
            try {
                number = Short.parseShort(formattedRegistrationPlate.substring(2, 4));
            } catch (NumberFormatException numberFormatException) {
                System.out.println(numberFormatException.getMessage());
            }
        }
        String matriculationNumber = formattedRegistrationPlate.substring(formattedRegistrationPlate.length() - 3);

        if (!municipalRegions.contains(municipalRegion) || number == null)
            throw new InvalidRoRegPlateException(FOREIGN_CAR);
        String finalMunicipalRegion = municipalRegion;

        Short finalNumber = number;
        return new RoRegPlateProperties() {
            @Override
            public Judet getJudet() {
                return Judet.valueOf(finalMunicipalRegion);
            }

            @Override
            public Short getDigits() {
                return finalNumber;
            }

            @Override
            public String getLetters() {
                return matriculationNumber;
            }
        };
    }
}
