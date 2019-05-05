package com.rmkrings.main.pius_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rmkrings.data.adapter.CalendarSearchListAdapter;
import com.rmkrings.data.calendar.Calendar;
import com.rmkrings.data.calendar.CalendarListItem;
import com.rmkrings.data.calendar.MonthHeaderItem;
import com.rmkrings.data.calendar.MonthItem;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;

/**
 */
public class CalendarSearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "calendar";

    // Outlets
    private EditText mSearchInput = null;
    private RecyclerView mDateList = null;
    private CalendarSearchListAdapter mCalendarSearchListAdapter = null;

    // Local state
    private Calendar calendar;
    private Calendar filteredCalendar;
    private ArrayList<CalendarListItem> listItems = new ArrayList<>();

    public CalendarSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param calendar Parameter 1.
     * @return A new instance of fragment CalendarSearchFragment.
     */
    public static CalendarSearchFragment newInstance(Calendar calendar) {
        CalendarSearchFragment fragment = new CalendarSearchFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, calendar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            calendar = (Calendar)getArguments().getSerializable(ARG_PARAM1);
            filteredCalendar = calendar;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSearchInput = view.findViewById(R.id.searchinput);
        mDateList = view.findViewById(R.id.datelist);

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filteredCalendar = calendar.filter(s.toString());
                setDateList();
            }
        });

        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mDateList.setLayoutManager(mVerticalLayoutManager);
        mCalendarSearchListAdapter = new CalendarSearchListAdapter(listItems);
        mDateList.setAdapter(mCalendarSearchListAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_search, container, false);
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
        setDateList();
    }

    private void setDateList() {
        listItems.clear();
        for (MonthItem montItem: filteredCalendar.getMonthItems()) {
            listItems.add(new MonthHeaderItem(montItem.getName()));
            listItems.addAll(montItem.getDayItems());
        }

        mCalendarSearchListAdapter.notifyDataSetChanged();
    }
}
