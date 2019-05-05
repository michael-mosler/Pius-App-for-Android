package com.rmkrings.data.calendar;

import java.io.Serializable;

public abstract class CalendarListItem implements Serializable {
    public static final int monthHeader = 0;
    public static final int dayItem = 1;
    public static final int message = 2;

    abstract public int getType();
}

