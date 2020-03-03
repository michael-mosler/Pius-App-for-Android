package com.rmkrings.fragments;

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
import com.rmkrings.helper.Config;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.ParentFragment;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.activities.R;
import com.rmkrings.pius_app_for_android;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 */
public class TodayCalendarFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private CalendarSearchListAdapter mCalendarSearchListAdapter;

    // Local State
    private final String digestFileName = Config.digestFilename("calendar");
    private final String cacheFileName = Config.cacheFilename("calendar");
    private final Cache cache = new Cache();
    private Calendar calendar;
    private final ArrayList<CalendarListItem> dateList = new ArrayList<>();
    private ParentFragment parentFragment;

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    /**
     * Required but not implemented.
     */
    public TodayCalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Called when view has been created. Creates fragment content.
     * @param view - View that has been created
     * @param savedInstanceState - Saved state, unused.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mDateList = view.findViewById(R.id.datelist);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(pius_app_for_android.getAppContext(), LinearLayoutManager.VERTICAL, false);

        mDateList.setLayoutManager(mVerticalLayoutManager);
        mDateList.addItemDecoration(new DividerItemDecoration(mDateList.getContext(), DividerItemDecoration.VERTICAL));
        mCalendarSearchListAdapter = new CalendarSearchListAdapter(dateList);
        mDateList.setAdapter(mCalendarSearchListAdapter);

        Objects.requireNonNull(getFragmentManager())
                .beginTransaction()
                .hide(this)
                .commit();
    }

    /**
     * When view is created inflate calendar fragment.
     * @param inflater - Layout inflater to use.
     * @param container - Container to inflate into.
     * @param savedInstanceState - Unused
     * @return Inflated view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_calendar, container, false);
    }

    /**
     * Interface: Show fragment with
     * @param parentFragment - Parent fragment to link to.
     */
    public void show(ParentFragment parentFragment) {
        this.parentFragment = parentFragment;
        reload();
    }

    /**
     * Checks if data can be refreshed and if there is data to show. If not fragment gets
     * hidden. Otherwise it is shown and date list is refreshed.
     */
    private void setDateList() {
        if (isAdded() && getFragmentManager() != null && !getFragmentManager().isStateSaved()) {
            ArrayList<DayItem> list = calendar.getTodayEvents();

            // Nothing in calendar for today. Hide calendar fragment.
            if (list.size() == 0) {
                Objects.requireNonNull(getFragmentManager())
                        .beginTransaction()
                        .hide(this)
                        .commit();
            } else {
                // Show calendar fragment and add content.
                Objects.requireNonNull(getFragmentManager())
                        .beginTransaction()
                        .show(this)
                        .commit();

                dateList.clear();
                for (DayItem dayItem : calendar.getTodayEvents()) {
                    dateList.add(new CalendarMessage(dayItem.getEvent()));
                }
                mCalendarSearchListAdapter.notifyDataSetChanged();
            }

            parentFragment.notifyDoneRefreshing();
        }
    }

    /**
     * Display a message in calendar fragment.
     * @param message - The message to display.
     */
    private void setMessage(String message) {
        Objects.requireNonNull(getFragmentManager())
                .beginTransaction()
                .show(this)
                .commit();

        dateList.clear();
        dateList.add(new CalendarMessage(message, Gravity.CENTER));
        mCalendarSearchListAdapter.notifyDataSetChanged();
        parentFragment.notifyDoneRefreshing();
    }

    /**
     * Reload calendar from backend.
     */
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

    /**
     * Calendar has been received from backend. Update calendar fragment. This might fail as
     * reply is received in background and user might have decided to navigate away from Today
     * view by closing the app or by opening another tab, meanwhile.
     * @param responseData - Response data object containing calendar data.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        try {
            String data;
            JSONObject jsonData;

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
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

            jsonData = new JSONObject(data);
            calendar = new Calendar(jsonData);

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && calendar.getDigest() != null) {
                cache.store(digestFileName, calendar.getDigest());
            }

            setDateList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
