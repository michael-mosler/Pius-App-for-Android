package com.rmkrings.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.helper.DateHelper;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.ParentFragment;
import com.rmkrings.layouts.StaffHelperPopover;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.activities.R;
import com.rmkrings.notifications.DashboardWidgetUpdateService;
import com.rmkrings.pius_app_for_android;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;


/**
 */
public class TodayFragment extends Fragment implements HttpResponseCallback, ParentFragment {

    // Outlets
    private SwipeRefreshLayout mFragment = null;
    private ProgressBar mProgressBar;
    private TodayPostingsFragment mTodayPostingsFragment = null;
    private TodayVertretungsplanFragment mTodayVertetungsplanFragment = null;
    private TodayCalendarFragment mTodayCalendarFragment = null;
    private TodayNewsFragment mTodayNewsFragment = null;

    // Local State
    private int pendingRefreshs;
    private String digestFileName() { return Config.digestFilename(AppDefaults.getGradeSetting()); }
    private String cacheFileName() { return Config.cacheFilename(AppDefaults.getGradeSetting()); }

    private final Cache cache = new Cache();

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    public TodayFragment() {
        // Required empty public constructor
        // AppDefaults.setHasConfirmedStaffHelper(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragment = view.findViewById(R.id.todayFragment);
        mFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload(true);
            }
        });

        mProgressBar = view.findViewById(R.id.progressBar);

        mTodayPostingsFragment = (TodayPostingsFragment)getChildFragmentManager().findFragmentById(R.id.postingsfragment);
        mTodayVertetungsplanFragment = (TodayVertretungsplanFragment)getChildFragmentManager().findFragmentById(R.id.vertretungsplanfragment);

        TextView mDate = view.findViewById(R.id.date);
        mTodayCalendarFragment = (TodayCalendarFragment)getChildFragmentManager().findFragmentById(R.id.calendarfragment);

        DateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMMM", Locale.GERMANY);
        mDate.setText(String.format("%s (%s-Woche)", dateFormat.format(new Date()), DateHelper.week()));

        mTodayNewsFragment = (TodayNewsFragment)getChildFragmentManager().findFragmentById(R.id.newsfragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).setTitle(R.string.title_home);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        reload(false);

        //If explanation staff popover has not been show then create it.

        if (Config.getAlwaysShowStaffPopoverHelper() || !AppDefaults.getHasConfirmedStaffHelper()) {
            StaffHelperPopover staffHelperPopover = new StaffHelperPopover(pius_app_for_android.getAppContext(), this.getView());
            staffHelperPopover.show();
        }
    }

    private void reload(boolean refreshing) {
        String digest;

        pendingRefreshs = 4;

        if (cache.fileExists(cacheFileName()) && cache.fileExists(digestFileName())) {
            digest = cache.read(digestFileName());
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName()));
            digest = null;
        }

        if (!refreshing) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        mTodayPostingsFragment.show(this);

        // We load Vertretungsplan from here as data might be needed in several child fragments.
        // If fragment tells us that dashboard cannot be used we do not load data but
        // call show with a null Vertretunsgplan. This will hide according box.
        if (Config.canUseDashboard()) {
            VertretungsplanLoader vertretungsplanLoader = new VertretungsplanLoader(AppDefaults.getGradeSetting());
            vertretungsplanLoader.load(this, digest);
        } else {
            mTodayVertetungsplanFragment.show((Vertretungsplan)null, this);
        }

        mTodayCalendarFragment.show(this);
        mTodayNewsFragment.show(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
                    logger.severe(String.format("Failed to load data for news. HTTP Status code %d.", responseData.getHttpStatusCode()));
                    mTodayVertetungsplanFragment.show(getResources().getString(R.string.error_failed_to_load_data), this);
                    return;
                }

                if (responseData.getData() != null) {
                    data = responseData.getData();
                    cache.store(cacheFileName(), data);
                } else {
                    data = cache.read(cacheFileName());
                }

                // Update widget when new data has been loaded.
                Context context = pius_app_for_android.getAppContext();
                Intent intent = new Intent(context, DashboardWidgetUpdateService.class);
                context.startService(intent);

                try {
                    jsonData = new JSONObject(data);
                    Vertretungsplan vertretungsplan = new Vertretungsplan(jsonData);

                    if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && vertretungsplan.getDigest() != null) {
                        cache.store(digestFileName(), vertretungsplan.getDigest());
                    }

                    mTodayVertetungsplanFragment.show(vertretungsplan, this);
                } catch (Exception e) {
                    mTodayVertetungsplanFragment.show(getResources().getString(R.string.error_failed_to_load_data), this);
                    e.printStackTrace();
                }
            }
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyDoneRefreshing() {
        pendingRefreshs -= 1;

        if (pendingRefreshs <= 0) {
            mFragment.setRefreshing(false);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
