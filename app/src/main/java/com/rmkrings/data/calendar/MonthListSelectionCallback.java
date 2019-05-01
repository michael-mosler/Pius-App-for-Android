package com.rmkrings.data.calendar;

import android.widget.Button;

public interface MonthListSelectionCallback {
    public void notifySelectionChanged(Button b, String monthName);
}
