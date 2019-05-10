package com.rmkrings.main.pius_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmkrings.data.adapter.CalendarSearchListAdapter;
import com.rmkrings.data.calendar.Calendar;
import com.rmkrings.data.calendar.CalendarListItem;
import com.rmkrings.data.calendar.CalendarMessage;
import com.rmkrings.data.calendar.DayItem;
import com.rmkrings.helper.Cache;
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.pius_app_for_android.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 */
public class TodayCalendarFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private CalendarSearchListAdapter mCalendarSearchListAdapter;

    // Local State
    private String digestFileName = "calendar.md5";
    private String cacheFileName = "calendar.json";
    private Cache cache = new Cache();
    private Calendar calendar;
    private ArrayList<CalendarListItem> dateList = new ArrayList<>();

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    public TodayCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mDateList = view.findViewById(R.id.datelist);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);

        mDateList.setLayoutManager(mVerticalLayoutManager);
        mDateList.addItemDecoration(new DividerItemDecoration(mDateList.getContext(), DividerItemDecoration.VERTICAL));
        mCalendarSearchListAdapter = new CalendarSearchListAdapter(dateList);
        mDateList.setAdapter(mCalendarSearchListAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_calendar, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void setDateList() {
        ArrayList<DayItem> list = calendar.getTodayEvents();
        if (list.size() == 0) {
            setMessage(getResources().getString(R.string.text_empty_calendar));
        } else {
            dateList.clear();

            for (DayItem dayItem: calendar.getTodayEvents()) {
                dateList.add(new CalendarMessage(dayItem.getEvent()));
            }
        }

        mCalendarSearchListAdapter.notifyDataSetChanged();
    }

    private void setMessage(String message) {
        dateList.clear();
        dateList.add(new CalendarMessage(message, Gravity.CENTER));
        mCalendarSearchListAdapter.notifyDataSetChanged();
    }

    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        CalendarLoader calendarLoader = new CalendarLoader();
        calendarLoader.load(this, digest);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        if (responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Calendar. HTTP Status code %d.", responseData.getHttpStatusCode()));
            setMessage(getResources().getString(R.string.error_failed_to_load_data));
            return;
        }

        if (responseData.getData() != null) {
            data = responseData.getData();
            cache.store(cacheFileName, data);
        } else {
            data = cache.read(cacheFileName);
        }

        try {
            jsonData = new JSONObject(data);
            calendar = new Calendar(jsonData);

            if (responseData.getHttpStatusCode() != 304 && calendar.getDigest() != null) {
                cache.store(digestFileName, calendar.getDigest());
            }

            setDateList();
        }
        catch (Exception e) {
            e.printStackTrace();
            setMessage(getResources().getString(R.string.error_failed_to_load_data));
        }
    }
}
