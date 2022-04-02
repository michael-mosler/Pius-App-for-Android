package com.rmkrings.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.rmkrings.data.adapter.CalendarSearchListAdapter;
import com.rmkrings.data.calendar.Calendar;
import com.rmkrings.data.calendar.CalendarListItem;
import com.rmkrings.data.calendar.MonthHeaderItem;
import com.rmkrings.data.calendar.MonthItem;
import com.rmkrings.activities.R;

import java.util.ArrayList;

/**
 */
public class CalendarSearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "calendar";

    private CalendarSearchListAdapter mCalendarSearchListAdapter = null;
    // private CalendarSearchLiLstAdapter mCalendarSearchListAdapter = null;

    // Local state
    private Calendar calendar;
    private Calendar filteredCalendar;
    private final ArrayList<CalendarListItem> listItems = new ArrayList<>();

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
        // Outlets
        SearchView mSearchInput = view.findViewById(R.id.searchinput);
        RecyclerView mDateList = view.findViewById(R.id.datelist);

        mSearchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filteredCalendar = calendar.filter(query);
                setDateList();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredCalendar = calendar.filter(newText);
                setDateList();
                return true;
            }
        });

        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void onResume() {
        super.onResume();
        setDateList();
    }

    private void setDateList() {
        if (filteredCalendar == null) {
            return;
        }

        mCalendarSearchListAdapter.notifyItemRangeRemoved(0, listItems.size());
        listItems.clear();
        for (MonthItem montItem: filteredCalendar.getMonthItems()) {
            listItems.add(new MonthHeaderItem(montItem.getName()));
            listItems.addAll(montItem.getDayItems());
        }

        mCalendarSearchListAdapter.notifyItemRangeInserted(0, listItems.size());
    }
}
