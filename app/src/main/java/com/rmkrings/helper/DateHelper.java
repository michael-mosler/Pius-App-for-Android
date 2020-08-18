package com.rmkrings.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class DateHelper {
    public static String week() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(Locale.GERMANY);
        return (gregorianCalendar.get(Calendar.WEEK_OF_YEAR) % 2 != 0) ? "A" : "B";
    }

    public static String convert(String date, String from, String to) {
        DateFormat dateFormatFrom = new SimpleDateFormat(from, Locale.GERMANY);
        DateFormat dateFormatTo = new SimpleDateFormat(to, Locale.GERMANY);

        try {
            Date d = dateFormatFrom.parse(date);
            return dateFormatTo.format(Objects.requireNonNull(d));
        }
        catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}
