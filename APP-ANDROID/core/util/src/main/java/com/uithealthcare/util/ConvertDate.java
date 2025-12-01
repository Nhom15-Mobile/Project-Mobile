package com.uithealthcare.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ConvertDate {

    public static String UStoDateVN(String date){
        String[] parts = date.split("-"); // [2025, 12, 11]
        return parts[2] + "-" + parts[1] + "-" + parts[0]; // "11-12-2025";
    }

    public static String ISOtoDateVN(String isoDate){

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date date = null;
        try {
            date = inputFormat.parse(isoDate);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (date != null){
            return outputFormat.format(date);
        }
        else return "Date is null";
    }

    public static String ISOtoDateUS(String isoDate){

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date = null;
        try {
            date = inputFormat.parse(isoDate);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (date != null){
            return outputFormat.format(date);
        }
        else return "Date is null";
    }
}
