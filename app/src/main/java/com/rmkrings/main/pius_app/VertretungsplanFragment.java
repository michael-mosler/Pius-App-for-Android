package com.rmkrings.main.pius_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

import com.rmkrings.data.adapter.MetaDataAdapter;
import com.rmkrings.data.adapter.VertretungsplanListAdapter;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.pius_app_for_android.R;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class VertretungsplanFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private SwipeRefreshLayout mFragment;
    private ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private TextView mLastUpdate;
    private ExpandableListView mVertretungsplanListView;
    private VertretungsplanListAdapter mVertretunsplanListAdapter;

    // Local state.
    private final String digestFileName = "vertretungsplan.md5";
    private final String cacheFileName = "vertretungsplan.json";

    private boolean mustCollapseGroups = false;
    private final Cache cache = new Cache();
    private Vertretungsplan vertretungsplan;
    private final String[] metaData = new String[2];
    private final ArrayList<String> listDataHeader = new ArrayList<>(0);
    private final HashMap<String, List<String>> listDataChild = new HashMap<>(0);
    private FragmentActivity fragmentActivity;

    private final static Logger logger = Logger.getLogger(VertretungsplanLoader.class.getName());

    public VertretungsplanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragment = view.findViewById(R.id.vertretungsplanFragment);
        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mMetaData = view.findViewById(R.id.metadata);
        mLastUpdate = view.findViewById(R.id.lastupdate);
        mVertretungsplanListView = view.findViewById(R.id.vertretungsplanListView);

        mMetaData.setHasFixedSize(true);

        // Create Meta Data output widgets.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        mMetaData.setLayoutManager(mLayoutManager);
        mAdapter = new MetaDataAdapter(metaData);
        mMetaData.setAdapter(mAdapter);

        // Prepare list data
        mVertretunsplanListAdapter = new VertretungsplanListAdapter(PiusApplication.getAppContext(), listDataHeader, listDataChild);
        mVertretungsplanListView.setAdapter(mVertretunsplanListAdapter);

        mVertretungsplanListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                VertretungsplanForDate vertretungsplanForDate = vertretungsplan.getVertretungsplaene().get(groupPosition);
                GradeItem gradeItem = vertretungsplanForDate.getGradeItems().get(childPosition);

                FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, VertretungsplanDetailFragment.newInstance(gradeItem, vertretungsplanForDate.getDate()));
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            }
        });

        mFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload(true);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vertretungsplan, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity)context;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppDefaults.isAuthenticated()) {
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_substitution_schedule))
                    .setMessage(getResources().getString(R.string.error_cannot_use_vertretungsplan))
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

        Objects.requireNonNull(getActivity()).setTitle(R.string.title_substitution_schedule);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        reload(false);
    }

    private void reload(boolean refreshing) {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        if (!refreshing) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        VertretungsplanLoader vertretungsplanLoader = new VertretungsplanLoader(null);
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
        for (VertretungsplanForDate vertretungsplanForDate: vertretungsplan.getVertretungsplaene()) {
            listDataHeader.add(vertretungsplanForDate.getDate());

            List<String> grades = new ArrayList<>(0);
            for (GradeItem gradeItem: vertretungsplanForDate.getGradeItems()) {
                grades.add(gradeItem.getGrade());
            }

            listDataChild.put(vertretungsplanForDate.getDate(), grades);
        }

        mVertretunsplanListAdapter.notifyDataSetChanged();

        for (int i = 0; mustCollapseGroups && i < listDataHeader.size(); i++) {
            mVertretungsplanListView.collapseGroup(i);
        }

        mustCollapseGroups = false;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        mFragment.setRefreshing(false);
        mProgressBar.setVisibility(View.INVISIBLE);

        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Vertretungsplan. HTTP Status code %d.", responseData.getHttpStatusCode()));
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_substitution_schedule))
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
            cache.store(cacheFileName, data);
        } else {
            data = cache.read(cacheFileName);
        }

        try {
            jsonData = new JSONObject(data);
            vertretungsplan = new Vertretungsplan(jsonData);

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && vertretungsplan.getDigest() != null) {
                cache.store(digestFileName, vertretungsplan.getDigest());
            }

            setMetaData();
            setLastUpdate();
            setVertretungsplanList();
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_substitution_schedule))
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
        }
    }
}
