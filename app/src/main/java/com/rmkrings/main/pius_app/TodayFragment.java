package com.rmkrings.main.pius_app;

import android.annotation.SuppressLint;
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

import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.DateHelper;
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.pius_app_for_android.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;


/**
 */
public class TodayFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    TextView mDate = null;
    TodayCalendarFragment mTodayCalendarFragment = null;
    TodayVertretungsplanFragment mTodayVertetungsplanFragment = null;

    // Local State
    private String grade;
    private String digestFileName() { return String.format("%s.md5", grade); }
    private String cacheFileName() { return String.format("%s.json", grade); }

    private Cache cache = new Cache();

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

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

        mTodayVertetungsplanFragment = (TodayVertretungsplanFragment) getChildFragmentManager().findFragmentById(R.id.vertretungsplanfragment);

        mDate = view.findViewById(R.id.date);
        mTodayCalendarFragment = (TodayCalendarFragment)getChildFragmentManager().findFragmentById(R.id.calendarfragment);

        DateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMMM", Locale.GERMANY);
        mDate.setText(String.format("%s (%s-Woche)", dateFormat.format(new Date()), DateHelper.week()));

        grade = AppDefaults.getGradeSetting();
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

        reload();
    }

    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName()) && cache.fileExists(digestFileName())) {
            digest = cache.read(digestFileName());
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName()));
            digest = null;
        }

        /*
        if (!refreshing) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        */

        VertretungsplanLoader vertretungsplanLoader = new VertretungsplanLoader(grade);
        vertretungsplanLoader.load(this, digest);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        if (responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for news. HTTP Status code %d.", responseData.getHttpStatusCode()));
            mTodayVertetungsplanFragment.show(getResources().getString(R.string.error_failed_to_load_data));
            return;
        }

        if (responseData.getData() != null) {
            data = responseData.getData();
            cache.store(cacheFileName(), data);
        } else {
            data = cache.read(cacheFileName());
        }

        try {
            jsonData = new JSONObject(data);
            Vertretungsplan vertretungsplan = new Vertretungsplan(jsonData);

            if (responseData.getHttpStatusCode() != 304 && vertretungsplan.getDigest() != null) {
                cache.store(digestFileName(), vertretungsplan.getDigest());
            }

            mTodayVertetungsplanFragment.show(vertretungsplan);
        }
        catch (Exception e) {
            mTodayVertetungsplanFragment.show(getResources().getString(R.string.error_failed_to_load_data));
            e.printStackTrace();
        }
    }
}
