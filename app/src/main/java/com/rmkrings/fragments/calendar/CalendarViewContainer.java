package com.rmkrings.fragments.calendar;

/**
 * Calendar view container must implement this interface to get notified when selection
 * has changed.
 */
public interface CalendarViewContainer {

    /**
     * Called when selected date has changed.
     * @param dayViewContainer Newly selected day view container.
     */
    void onSelectionChanged(DayViewContainer dayViewContainer);

}
