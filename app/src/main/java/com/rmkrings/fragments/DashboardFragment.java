package com.rmkrings.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rmkrings.activities.MainActivity;
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
import com.rmkrings.fragments.preferences.PreferencesFragment;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.activities.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static androidx.recyclerview.widget.RecyclerView.*;

public class DashboardFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private SwipeRefreshLayout mFragment;
    private ProgressBar mProgressBar;
    private Adapter<?> mMetaDataAdapter;
    public TextView mLastUpdate;
    private ExpandableListView mDashboardListView;
    private ImageButton mEvaButton;
    private DashboardListAdapter mDashboardListAdapter;
    private FragmentActivity fragmentActivity;

    // Local state.
    private String grade;
    private Boolean reloadOnResume = true;

    private String digestFileName() {
        return Config.digestFilename(grade);
    }

    private String cacheFileName() {
        return Config.cacheFilename(grade);
    }

    private final Cache cache = new Cache();
    private Vertretungsplan vertretungsplan;
    private final String[] metaData = new String[2];
    private final ArrayList<String> listDataHeader = new ArrayList<>(0);
    private final HashMap<String, List<VertretungsplanListItem>> listDataChild = new HashMap<>(0);

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
        mFragment = view.findViewById(R.id.swipeRefreshLayout);
        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mMetaData = view.findViewById(R.id.metadata);
        mLastUpdate = view.findViewById(R.id.lastupdate);
        mDashboardListView = view.findViewById(R.id.vertretungsplanListView);

        mMetaData.setHasFixedSize(true);

        // Create Meta Data output widgets.
        LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mMetaData.setLayoutManager(mLayoutManager);
        mMetaDataAdapter = new MetaDataAdapter(metaData);
        mMetaData.setAdapter(mMetaDataAdapter);

        // Prepare list data
        mDashboardListAdapter = new DashboardListAdapter(getActivity(), listDataHeader, listDataChild);
        mDashboardListView.setAdapter(mDashboardListAdapter);

        mEvaButton = view.findViewById(R.id.evaButton);
        mEvaButton.setOnClickListener(v -> {
            FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new EvaFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        mFragment.setOnRefreshListener(() -> reload(true));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity) context;
        reloadOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        grade = AppDefaults.getGradeSetting();
        mEvaButton.setVisibility(AppDefaults.hasUpperGrade() ? View.VISIBLE : View.INVISIBLE);

        if (!canUseDashboard()) {
            new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_dashboard))
                    .setMessage(getResources().getString(R.string.error_cannot_use_dashboard))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        if (getParentFragmentManager() != null) {
                            getParentFragmentManager().popBackStack();
                        }
                    })
                    .setNegativeButton(R.string.title_settings, (dialog, which) -> {
                        // FragmentTransaction t =
                        MainActivity activity = (MainActivity)requireActivity();
                        activity.startFragment(new PreferencesFragment(), true);
                    })
                    .show();

            return;
        }

        requireActivity().setTitle(grade);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(2).setChecked(true);
        mNavigationView.getMenu().getItem(2).setTitle(grade);

        if (reloadOnResume) {
            reload(false);
        } else {
            setLastUpdate();
        }

        reloadOnResume = false;
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

    @SuppressLint("NotifyDataSetChanged")
    private void setMetaData() {
        if (vertretungsplan != null) {
            if ((vertretungsplan.getAdditionalText().length() + vertretungsplan.getTickerText().length()) < 200){
                this.metaData[0] = vertretungsplan.getTickerText() + "\n" + vertretungsplan.getAdditionalText();
                this.metaData[1] = null;
            } else {
                this.metaData[0] = vertretungsplan.getTickerText();
                this.metaData[1] = vertretungsplan.getAdditionalText();
            }

            mMetaDataAdapter.notifyDataSetChanged();
        }
    }

    private void setLastUpdate() {
        if (vertretungsplan != null) {
            mLastUpdate.setText(vertretungsplan.getLastUpdate());
        }
    }

    private void setVertretungsplanList() {
        Date currentDate = new Date();
        boolean[] expanded = new boolean[vertretungsplan.getVertretungsplaene().size()];
        boolean hasExpanded = false;
        int i = 0;

        listDataHeader.clear();
        listDataChild.clear();
        for (VertretungsplanForDate vertretungsplanForDate : vertretungsplan.getVertretungsplaene()) {
            listDataHeader.add(vertretungsplanForDate.getDate());
            expanded[i] = false;

            ArrayList<VertretungsplanListItem> vertretungsplanListItems = new ArrayList<>(0);

            // There is only one grade item when in dashboard mode.
            for (GradeItem g : vertretungsplanForDate.getGradeItems()) {
                for (String[] a : g.getVertretungsplanItems()) {
                    // Check if the current item can be accepted, i.e. must be displayed.
                    VertretungsplanHeaderItem headerItem = new VertretungsplanHeaderItem(a[2], a[0]);
                    if (headerItem.accept()) {
                        // We will expand the next date with a substitution, thus that date that has
                        // a lesson which is after current date and time. Once such a lesson has been
                        // found no more groups shall get expanded. This is why we need to track
                        // expansion.
                        if (!hasExpanded) {
                            Date lessonStartDate = headerItem.realLessonStartDate(vertretungsplanForDate.getDate());
                            expanded[i] |= (lessonStartDate.after(currentDate) || lessonStartDate.equals(currentDate));
                            hasExpanded = expanded[i];
                        }

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
            }

            listDataChild.put(vertretungsplanForDate.getDate(), vertretungsplanListItems);
            i += 1;
        }

        mDashboardListAdapter.notifyDataSetChanged();

        for (i = 0; i < listDataHeader.size(); i++) {
            if (expanded[i]) {
                mDashboardListView.expandGroup(i);
            } else {
                mDashboardListView.collapseGroup(i);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        mFragment.setRefreshing(false);
        mProgressBar.setVisibility(View.INVISIBLE);

        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
                    logger.severe(String.format("Failed to load data for Dashboard. HTTP Status code %d.", responseData.getHttpStatusCode()));
                    new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                            .setTitle(getResources().getString(R.string.title_dashboard))
                            .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                if (getParentFragmentManager() != null) {
                                    getParentFragmentManager().popBackStack();
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

                    if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && vertretungsplan.getDigest() != null) {
                        cache.store(digestFileName(), vertretungsplan.getDigest());
                    }

                    setMetaData();
                    setLastUpdate();
                    setVertretungsplanList();
                } catch (Exception e) {
                    onInternalError(e);
                }
            }
        } catch (IllegalStateException e) {
            onInternalError(e);
        }
    }

    @Override
    public void onInternalError(Exception e) {
        new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(getResources().getString(R.string.title_dashboard))
                .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().popBackStack();
                    }
                })
                .show();
    }

    private boolean canUseDashboard() {
        return AppDefaults.isAuthenticated() && AppDefaults.hasGrade();
    }

}
