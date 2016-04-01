package pl.sebcel.gpstracker.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFormat {

    private static String format(int number, int length) {
        String stringValue = Integer.toString(number);
        while (stringValue.length() < length) {
            stringValue = "0" + stringValue;
        }
        return stringValue;
    }

    private static String d2(int number) {
        return format(number, 2);
    }

    private static String d4(int number) {
        return format(number, 4);
    }

    private static String d5(int number) {
        return format(number, 5);
    }

    public static String format(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);
        return d4(year) + "-" + d2(month) + "-" + d2(day) + "T" + d2(hour) + ":" + d2(minute) + ":" + d2(second) + "." + d5(millisecond) + "Z";
    }
    
    public static String endomondoFormat(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        return d4(year) + "-" + d2(month) + "-" + d2(day) + " " + d2(hour) + ":" + d2(minute) + ":" + d2(second) + " GMT+00:00";
    }

    public static String getFilename(Date date, String suffix, String extension) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        return d4(year) + "-" + d2(month) + "-" + d2(day) + "_" + d2(hour) + "-" + d2(minute) + "-" + d2(second) + suffix + "." + extension;
    }
}