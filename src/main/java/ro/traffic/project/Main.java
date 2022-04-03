package ro.traffic.project;


import ro.traffic.project.api.RoIdCardParser;
import ro.traffic.project.api.RoIdCardSeriesJudMapper;
import ro.traffic.project.api.RoRegPlateParser;
import ro.traffic.project.api.VehicleOwnersProcessor;
import ro.traffic.project.impl.*;

import java.io.*;
import java.util.Date;

public class Main {

    private static final String RECORD_CSV = "policeRecord.csv";
    private static final String RESULT = "result.ser";
    private static final String REFERENCE_DATE = "2020-02-21";

    public static void main(String[] args) {
        RoIdCardSeriesJudMapper roIdCardSeriesJudMapper = new RoIdCardSeriesJudMapperImpl();
        RoIdCardParser roIdCardParser = new RoIdCardParserImpl(roIdCardSeriesJudMapper);
        RoRegPlateParser roRegPlateParser = new RoRegPlateParserImpl();
        InputStream inputStream = null;
        Date ref = null;
        try {
            ref = VehicleOwnerAggregator.getFormattedDate(REFERENCE_DATE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            inputStream = new FileInputStream(RECORD_CSV);

        } catch (IOException e) {
            e.printStackTrace();
        }
        VehicleOwnersProcessor vehicleOwnersProcessor = new VehicleOwnersProcessorImpl(roIdCardParser, roRegPlateParser,
                ref);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(RESULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            vehicleOwnersProcessor.process(inputStream, outputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
