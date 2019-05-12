package com.rmkrings.main.pius_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.adapter.VertetungsplanDetailListAdapter;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.interfaces.ParentFragment;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;
import java.util.Objects;


public class TodayVertretungsplanFragment extends Fragment {
    // Outlets
    RecyclerView mItemList;
    VertetungsplanDetailListAdapter mVertetungsplanDetailListAdapter;

    // Local State
    private ArrayList<BaseListItem> listItems = new ArrayList<>();

    public TodayVertretungsplanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mItemList = view.findViewById(R.id.itemlist);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mItemList.setLayoutManager(mLayoutManager);
        mVertetungsplanDetailListAdapter = new VertetungsplanDetailListAdapter(listItems);
        mItemList.setAdapter(mVertetungsplanDetailListAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_vertretungsplan, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean canUseDashboard(){
        return (AppDefaults.isAuthenticated() && (AppDefaults.hasLowerGrade() || (AppDefaults.hasUpperGrade() && AppDefaults.getCourseList().size() > 0)));
    }

    public void show(String message, ParentFragment parentFragment) {
        if (!canUseDashboard()) {
            Objects.requireNonNull(getFragmentManager())
                    .beginTransaction()
                    .hide(this)
                    .commit();
        } else {
            Objects.requireNonNull(getFragmentManager())
                    .beginTransaction()
                    .show(this)
                    .commit();

            listItems.clear();
            listItems.add(new MessageItem(message, Gravity.CENTER));
            mVertetungsplanDetailListAdapter.notifyDataSetChanged();
        }

        parentFragment.notifyDoneRefreshing();
    }

    public void show(Vertretungsplan vertretungsplan, ParentFragment parentFragment) {
        VertretungsplanForDate vertretungsplanForDate = vertretungsplan.getTodaysSchedule();

        if (vertretungsplanForDate == null) {
            show(getResources().getString(R.string.text_empty_schedule), parentFragment);
            return;
        }

        if (vertretungsplanForDate.getGradeItems().size() == 0) {
            show(getResources().getString(R.string.text_empty_schedule), parentFragment);
            return;
        }

        listItems.clear();
        // There is only one grade item when in dashboard mode.
        for (GradeItem g: vertretungsplanForDate.getGradeItems()) {
            for (String[] a: g.getVertretungsplanItems()) {
                // Check if the current item can be accepted, i.e. must be displayed.
                VertretungsplanHeaderItem headerItem = new VertretungsplanHeaderItem(a[2], a[0]);
                if (headerItem.accept()) {
                    VertretungsplanDetailItem detailItem = new VertretungsplanDetailItem(a[1], a[3], a[4]);
                    VertretungsplanRemarkItem remarkItem = new VertretungsplanRemarkItem(a[6]);

                    listItems.add(headerItem);
                    listItems.add(detailItem);

                    if (remarkItem.getRemarkText().length() > 0) {
                        listItems.add(remarkItem);
                    }

                    if (a.length > 7) {
                        VertretungsplanEvaItem evaItem = new VertretungsplanEvaItem(a[7]);
                        listItems.add(evaItem);
                    }
                }
            }
        }

        if (listItems.size() == 0) {
            show(getResources().getString(R.string.text_empty_schedule), parentFragment);
            return;
        }

        mVertetungsplanDetailListAdapter.notifyDataSetChanged();
        parentFragment.notifyDoneRefreshing();
    }
}
