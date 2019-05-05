package com.rmkrings.main.pius_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.helper.DateHelper;
import com.rmkrings.pius_app_for_android.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 */
public class TodayFragment extends Fragment {
    // Outlets
    TextView mDate = null;
    TodayCalendarFragment mTodayCalendarFragment = null;

    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDate = view.findViewById(R.id.date);
        mTodayCalendarFragment = (TodayCalendarFragment)getChildFragmentManager().findFragmentById(R.id.calendarfragment);

        DateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMMM", Locale.GERMANY);
        mDate.setText(String.format("%s (%s-Woche)", dateFormat.format(new Date()), DateHelper.week()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).setTitle(R.string.title_home);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }
}
