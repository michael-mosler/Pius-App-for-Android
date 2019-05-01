package com.rmkrings.data.calendar;

import android.widget.Button;

public interface MonthListSelectionCallback {
    void notifySelectionChanged(Button b, String monthName);
}
