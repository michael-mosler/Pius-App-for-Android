package com.rmkrings.fragments.calendar;

import android.view.View;
import android.widget.TextView;

import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.ui.ViewContainer;
import com.rmkrings.activities.R;

public class DayViewContainer extends ViewContainer {

    public TextView textView;
    public CalendarDay calendarDay;
    public CalendarViewContainer parent;
    public boolean isSelected = false;

    public DayViewContainer(View view) {
        super(view);

        this.textView = view.findViewById(R.id.calendarDayText);

        final DayViewContainer instance = this;
        view.setOnClickListener(view1 -> parent.onSelectionChanged(instance));
    }

}
