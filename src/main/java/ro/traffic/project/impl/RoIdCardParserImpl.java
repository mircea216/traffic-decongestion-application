package ro.traffic.project.impl;

import ro.traffic.project.api.*;

import java.util.*;

public class RoIdCardParserImpl implements RoIdCardParser {
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final int PREDEFINED_CI_LENGTH = 8;
    private static final int START_COUNTY = 0;
    private static final int END_COUNTY = 2;
    private static final String INVALID_IDENTITY_CARD = "Invalid identity card";
    private final RoIdCardSeriesJudMapper roIdCardSeriesJudMapper;

    public RoIdCardParserImpl(RoIdCardSeriesJudMapper roIdCardSeriesJudMapper) {
        this.roIdCardSeriesJudMapper = roIdCardSeriesJudMapper;
    }

    @Override
    public RoIdCardProperties parseIdCard(String idCard) throws InvalidRoIdCardException {
        String formattedIdCard = idCard.trim().replaceAll(SPACE, EMPTY_STRING).toUpperCase(Locale.ROOT);
        if (formattedIdCard.length() < PREDEFINED_CI_LENGTH)
            throw new InvalidRoIdCardException(INVALID_IDENTITY_CARD);
        String series = formattedIdCard.substring(START_COUNTY, END_COUNTY);
        Integer number = null;
        Judet county = roIdCardSeriesJudMapper.mapIdCardToJud(idCard);
        try {
            number = Integer.parseInt(formattedIdCard.substring(END_COUNTY));
        } catch (NumberFormatException numberFormatException) {
            System.out.println(numberFormatException.getMessage());
        }
        if (number == null || county == null)
            throw new InvalidRoIdCardException(INVALID_IDENTITY_CARD);
        Integer finalNumber = number;
        return new RoIdCardProperties() {
            @Override
            public Judet getJudet() {
                return county;
            }

            @Override
            public String getSeries() {
                return series;
            }

            @Override
            public Integer getNumber() {
                return finalNumber;
            }
        };
    }
}
