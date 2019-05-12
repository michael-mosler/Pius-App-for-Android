package com.rmkrings.data.calendar;

public class MonthHeaderItem extends CalendarListItem {
    private final String monthName;

    public MonthHeaderItem(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthName() {
        return monthName;
    }

    @Override
    public int getType() {
        return monthHeader;
    }
}
