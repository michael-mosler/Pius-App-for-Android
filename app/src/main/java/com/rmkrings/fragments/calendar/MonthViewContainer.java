package com.rmkrings.fragments.calendar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kizitonwose.calendarview.ui.ViewContainer;
import com.rmkrings.activities.R;

public class MonthViewContainer extends ViewContainer {
    public TextView textView;
    public Button todayButton;
    public ImageButton searchButton;

    public MonthViewContainer(View view) {
        super(view);
        textView = view.findViewById(R.id.headerTextView);
        todayButton = view.findViewById(R.id.buttonGoToToday);
        searchButton = view.findViewById(R.id.buttonSearch);
    }
}
