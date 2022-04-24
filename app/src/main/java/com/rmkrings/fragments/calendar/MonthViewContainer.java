package com.rmkrings.fragments.calendar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kizitonwose.calendarview.ui.ViewContainer;
import com.rmkrings.activities.R;
import com.rmkrings.data.calendar.Calendar;

import java.util.Observable;
import java.util.Observer;

/**
 * Container which holds name of month in format MON YYYY together with a
 * "Today" and search button. "Today" navigates back to current date when pressed.
 * Search starts calendar search.
 */
public class MonthViewContainer extends ViewContainer implements Observer {

    public TextView textView;
    public Button todayButton;
    public ImageButton searchButton;

    public MonthViewContainer(View view) {
        super(view);
        textView = view.findViewById(R.id.headerTextView);
        todayButton = view.findViewById(R.id.buttonGoToToday);
        searchButton = view.findViewById(R.id.buttonSearch);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Calendar)) {
            return;
        }

        Calendar calendar = (Calendar) o;
        if (calendar.getMonthItems().size() > 0) {
            searchButton.setEnabled(true);
            searchButton.setImageAlpha(255);
        } else {
            searchButton.setEnabled(false);
            searchButton.setImageAlpha(75);
        }
    }
}
