package com.rmkrings.PiusApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rmkrings.data.adapter.DashboardListAdapter;
import com.rmkrings.data.adapter.MetaDataAdapter;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.main.PiusApp;
import com.rmkrings.pius_app_for_android.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class DashboardFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private SwipeRefreshLayout mFragment;
    private ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private TextView mLastUpdate;
    private DashboardListAdapter mDashboardListAdapter;

    // Local state.
    private String grade;
    private String digestFileName() { return String.format("%s.md5", grade); };
    private String cacheFileName() { return String.format("%s.json", grade); }

    private Cache cache = new Cache();
    private Vertretungsplan vertretungsplan;
    private String[] metaData = new String[2];
    private ArrayList<String> listDataHeader = new ArrayList<>(0);
    private HashMap<String, List<VertretungsplanListItem>> listDataChild = new HashMap<>(0);
    private FragmentActivity fragmentActivity;

    private final static Logger logger = Logger.getLogger(VertretungsplanLoader.class.getName());

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragment = view.findViewById(R.id.dashboardFragment);
        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mMetaData = view.findViewById(R.id.metadata);
        mLastUpdate = view.findViewById(R.id.lastupdate);
        ExpandableListView mDashboardListView = view.findViewById(R.id.vertretungsplanListView);

        mMetaData.setHasFixedSize(true);

        // Create Meta Data output widgets.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PiusApp.getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        mMetaData.setLayoutManager(mLayoutManager);
        mAdapter = new MetaDataAdapter(metaData);
        mMetaData.setAdapter(mAdapter);

        // Prepare list data
        mDashboardListAdapter = new DashboardListAdapter(PiusApp.getAppContext(), listDataHeader, listDataChild);
        mDashboardListView.setAdapter(mDashboardListAdapter);

        grade = AppDefaults.getGradeSetting();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
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

        Objects.requireNonNull(getActivity()).setTitle(grade);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(2).setChecked(true);

        reload(false);
    }

    private void reload(boolean refreshing) {
        String digest;

        if (cache.fileExists(cacheFileName()) && cache.fileExists(digestFileName())) {
            digest = cache.read(digestFileName());
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName()));
            digest = null;
        }

        if (!refreshing) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        VertretungsplanLoader vertretungsplanLoader = new VertretungsplanLoader(grade);
        vertretungsplanLoader.load(this, digest);
    }

    private void setMetaData() {
        this.metaData[0] = vertretungsplan.getTickerText();
        this.metaData[1] = vertretungsplan.getAdditionalText();
        mAdapter.notifyDataSetChanged();
    }

    private void setLastUpdate() {
        mLastUpdate.setText(vertretungsplan.getLastUpdate());
    }

    private void setVertretungsplanList() {
        listDataHeader.clear();
        listDataChild.clear();
        for (VertretungsplanForDate vertretungsplanForDate: vertretungsplan.getVertretungsplaene()) {
            listDataHeader.add(vertretungsplanForDate.getDate());

            ArrayList<VertretungsplanListItem> vertretungsplanListItems = new ArrayList<>(0);

            // There is only one grade item when in dashboard mode.
            for (GradeItem g: vertretungsplanForDate.getGradeItems()) {
                for (String[] a: g.getVertretungsplanItems()) {
                    VertretungsplanHeaderItem headerItem = new VertretungsplanHeaderItem(a[2], a[0]);
                    VertretungsplanDetailItem detailItem = new VertretungsplanDetailItem(a[1], a[3], a[4]);
                    VertretungsplanRemarkItem remarkItem = new VertretungsplanRemarkItem(a[6]);

                    vertretungsplanListItems.add(headerItem);
                    vertretungsplanListItems.add(detailItem);

                    if (remarkItem.getRemarkText().length() > 0) {
                        vertretungsplanListItems.add(remarkItem);
                    }

                    if (a.length > 7) {
                        VertretungsplanEvaItem evaItem = new VertretungsplanEvaItem(a[7]);
                        vertretungsplanListItems.add(evaItem);
                    }
                }
            }

            listDataChild.put(vertretungsplanForDate.getDate(), vertretungsplanListItems);
        }

        mDashboardListAdapter.notifyDataSetChanged();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        mFragment.setRefreshing(false);
        mProgressBar.setVisibility(View.INVISIBLE);

        if (responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Dashboard. HTTP Status code %d.", responseData.getHttpStatusCode()));
            new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setTitle(getResources().getString(R.string.title_dashboard))
                    .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getFragmentManager() != null) {
                                getFragmentManager().popBackStack();
                            }
                        }
                    })
                    .show();
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
            vertretungsplan = new Vertretungsplan(jsonData);

            if (responseData.getHttpStatusCode() != 304 && vertretungsplan.getDigest() != null) {
                cache.store(digestFileName(), vertretungsplan.getDigest());
            }

            setMetaData();
            setLastUpdate();
            setVertretungsplanList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
