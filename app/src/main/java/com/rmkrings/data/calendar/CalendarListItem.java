package com.rmkrings.data.calendar;

import java.io.Serializable;

/**
 * Every calendar item type that is used in lists, such as day items, month header items and
 * messages must implement this interface.
 */
public abstract class CalendarListItem implements Serializable {
    public static final int monthHeader = 0;
    public static final int dayItem = 1;
    public static final int message = 2;

    abstract public int getType();
}

