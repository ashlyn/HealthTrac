package com.group7.healthtrac.services.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.TimeZone;

public class Utility {

    private static SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
    private static SimpleDateFormat sdfDisplay = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat sdfJSON = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat sdfDisplayWithTime = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public static Date parseDateFromDisplayToUtc(String date) throws ParseException {
        Date tempDate = sdfDisplay.parse(date);
        return sdfUtc.parse(sdfUtc.format(tempDate));
    }

    public static Date parseDateFromDisplayToUtc(Date date) throws ParseException {
        return sdfUtc.parse(sdfUtc.format(date));
    }

    public static Date parseDateFromJSONToDisplay(String date) throws ParseException {
        Date tempDate = sdfJSON.parse(date);
        return sdfDisplay.parse(sdfDisplay.format(tempDate));
    }

    public static Date parseDateFromJSONToUtc(String date) throws ParseException {
        Date tempDate = sdfJSON.parse(date);
        return sdfUtc.parse(sdfUtc.format(tempDate));
    }

    public static Date parseDateFromUtcToDisplay(String date) throws ParseException {
        Date tempDate = sdfUtc.parse(date);
        return sdfDisplay.parse(sdfDisplay.format(tempDate));
    }

    public static String displayDate(Date date) {
        return sdfDisplay.format(date);
    }

    public static String displayUtcDate(Date date) {
        return sdfUtc.format(date);
    }

    public static String displayTime(Date date) {
        return sdfTime.format(date);
    }

    public static String displayTime(double numSeconds) {
        int hours = (int) numSeconds / 3600;
        int minutes = (int) (numSeconds - (numSeconds / 3600)) / 60;
        int seconds = (int) numSeconds - (hours * 3600) - (minutes * 60);
        return hours + ":" + minutes + ":" + seconds;
    }

    public static String displayDateAndTime(Date date) {
        sdfDisplayWithTime.setTimeZone(TimeZone.getDefault());

        return sdfDisplayWithTime.format(date);
    }

    public static int parseTime(String time) throws IllegalArgumentException, TimeOutOfBoundsException {
        String[] timeComponents = time.split(":");
        int timeInSeconds = 0;
        int seconds;
        int minutes;
        int hours;

        if (timeComponents.length > 3 || timeComponents.length < 2) {
            throw new IllegalArgumentException();
        } else if (timeComponents.length == 3) {
            seconds = Integer.parseInt(timeComponents[2]);
            minutes = Integer.parseInt(timeComponents[1]);
            hours = Integer.parseInt(timeComponents[0]);
        } else {
            hours = 0;
            minutes = Integer.parseInt(timeComponents[0]);
            seconds = Integer.parseInt(timeComponents[1]);
        }

        timeInSeconds = seconds + (minutes * 60) + (hours * 60 * 60);

        return timeInSeconds;
    }
}
