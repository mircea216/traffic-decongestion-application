package ro.traffic.project.impl;

import org.yaml.snakeyaml.Yaml;
import ro.traffic.project.api.InvalidRoIdCardException;
import ro.traffic.project.api.Judet;
import ro.traffic.project.api.RoIdCardSeriesJudMapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

public class RoIdCardSeriesJudMapperImpl implements RoIdCardSeriesJudMapper {
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final String MAPPER_YML = "mapper.yml";
    private static final int PREDEFINED_CI_LENGTH = 8;
    private static final int START_COUNTY = 0;
    private static final int END_COUNTY = 2;
    private static final String INVALID_IDENTITY_CARD = "Invalid Identity Card";

    public static Map<String, Object> data;

    static {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(MAPPER_YML);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        data = yaml.load(inputStream);
    }

    @Override
    public Judet mapIdCardToJud(String idCardSeries) throws InvalidRoIdCardException {
        String formattedIdCard = idCardSeries.trim().replaceAll(SPACE, EMPTY_STRING).toUpperCase(Locale.ROOT);
        if (formattedIdCard.length() < PREDEFINED_CI_LENGTH)
            throw new InvalidRoIdCardException(INVALID_IDENTITY_CARD);
        String series = formattedIdCard.substring(START_COUNTY, END_COUNTY);
        boolean mappedSeries = false;
        String municipalRegion = EMPTY_STRING;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue().toString().contains(series)) {
                mappedSeries = true;
                municipalRegion = entry.getKey();
                break;
            }
        }
        if (!mappedSeries)
            throw new InvalidRoIdCardException(INVALID_IDENTITY_CARD);
        return Judet.valueOf(municipalRegion);
    }
}
