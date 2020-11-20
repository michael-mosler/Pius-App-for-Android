package com.rmkrings.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rmkrings.activities.SettingsActivity;
import com.rmkrings.data.adapter.MetaDataAdapter;
import com.rmkrings.data.adapter.VertretungsplanListAdapter;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.activities.R;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;
import com.rmkrings.pius_app_for_android;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static androidx.recyclerview.widget.RecyclerView.*;

public class VertretungsplanFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private SwipeRefreshLayout mFragment;
    private ProgressBar mProgressBar;
    private Adapter<?> mAdapter;
    private TextView mLastUpdate;
    private ExpandableListView mVertretungsplanListView;
    private VertretungsplanListAdapter mVertretunsplanListAdapter;

    // Local state.
    private Boolean reloadOnResume = true;
    private final String digestFileName = Config.digestFilename("vertretungsplan");
    private final String cacheFileName = Config.cacheFilename("vertretungsplan");

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
        mFragment = view.findViewById(R.id.swipeRefreshLayout);
        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mMetaData = view.findViewById(R.id.metadata);
        mLastUpdate = view.findViewById(R.id.lastupdate);
        mVertretungsplanListView = view.findViewById(R.id.vertretungsplanListView);

        mMetaData.setHasFixedSize(true);

        // Create Meta Data output widgets.
        LayoutManager mLayoutManager = new LinearLayoutManager(pius_app_for_android.getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        mMetaData.setLayoutManager(mLayoutManager);
        mAdapter = new MetaDataAdapter(metaData);
        mMetaData.setAdapter(mAdapter);

        // Prepare list data
        mVertretunsplanListAdapter = new VertretungsplanListAdapter(pius_app_for_android.getAppContext(), listDataHeader, listDataChild);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity)context;
        reloadOnResume = true;
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
                    .setNegativeButton(R.string.title_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fragmentActivity.startActivity(new Intent(fragmentActivity, SettingsActivity.class));
                        }
                    })
                    .show();
            return;
        }

        Objects.requireNonNull(getActivity()).setTitle(R.string.title_substitution_schedule);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        if (reloadOnResume) {
            reload(false);
        } else {
            setLastUpdate();
        }

        reloadOnResume = false;
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
        if (vertretungsplan != null) {
            this.metaData[0] = vertretungsplan.getTickerText();
            this.metaData[1] = vertretungsplan.getAdditionalText();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setLastUpdate() {
        if (vertretungsplan != null) {
            mLastUpdate.setText(vertretungsplan.getLastUpdate());
        }
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

        for (int i = 0; i < listDataHeader.size(); i++) {
            mVertretungsplanListView.collapseGroup(i);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        mFragment.setRefreshing(false);
        mProgressBar.setVisibility(View.INVISIBLE);

        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            if (getActivity() != null && !getActivity().isFinishing()) {
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
            }
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
            if (getActivity() != null && !getActivity().isFinishing()) {
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
}
