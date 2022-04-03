package ro.traffic.project.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VehicleOwnerAggregator {
    private static final String PATTERN = "yyyy-MM-dd";
    private static final String INVALID_DATE = "The date is not valid";
    private final Date referenceDate;

    public VehicleOwnerAggregator(Date referenceDate) {
        this.referenceDate = referenceDate;
    }


    public static Date getFormattedDate(String date) throws Exception {
        Date formattedDate = null;
        try {
            formattedDate = new SimpleDateFormat(PATTERN).parse(date);
        } catch (ParseException exception) {
            System.out.println(exception.getMessage());
        }
        if (formattedDate == null)
            throw new Exception(INVALID_DATE);
        return formattedDate;
    }
}
