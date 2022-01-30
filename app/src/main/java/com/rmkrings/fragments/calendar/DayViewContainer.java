package com.rmkrings.fragments.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.ui.ViewContainer;
import com.rmkrings.activities.R;

/**
 * Container which holds a single date in calendar view. It shows
 * the day of month and a marker view which should display not more
 * than 3 dots each dot indicating an event scheduled for the date.
 */
public class DayViewContainer extends ViewContainer {

    public TextView textView;
    public ImageView[] dotViews;
    public CalendarDay calendarDay;
    public CalendarViewContainer parent;

    public DayViewContainer(View view) {
        super(view);

        this.textView = view.findViewById(R.id.calendarDayText);
        this.dotViews = new ImageView[] {
                view.findViewById(R.id.imageView1),
                view.findViewById(R.id.imageView2),
                view.findViewById(R.id.imageView3)
        };

        final DayViewContainer instance = this;
        view.setOnClickListener(view1 -> parent.onSelectionChanged(instance));
    }

}
