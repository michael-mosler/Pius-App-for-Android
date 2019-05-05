package com.rmkrings.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {
    public static String week() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(Locale.GERMANY);
        return (gregorianCalendar.get(Calendar.WEEK_OF_YEAR) % 2 != 0) ? "A" : "B";
    }

}
