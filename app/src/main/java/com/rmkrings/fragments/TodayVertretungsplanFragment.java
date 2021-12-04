package com.rmkrings.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.rmkrings.interfaces.ParentFragment;
import com.rmkrings.activities.R;

import java.util.ArrayList;


public class TodayVertretungsplanFragment extends Fragment {
    private VertetungsplanDetailListAdapter mVertetungsplanDetailListAdapter;

    // Local State
    private final ArrayList<BaseListItem> listItems = new ArrayList<>();

    public TodayVertretungsplanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Outlets
        RecyclerView mItemList = view.findViewById(R.id.itemlist);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void show(String message, ParentFragment parentFragment) {
        if (getParentFragmentManager() != null && !getParentFragmentManager().isStateSaved()) {
            getParentFragmentManager()
                    .beginTransaction()
                    .show(this)
                    .commit();

            mVertetungsplanDetailListAdapter.notifyItemRangeRemoved(0, listItems.size());
            listItems.clear();
            listItems.add(new MessageItem(message, Gravity.CENTER));
            mVertetungsplanDetailListAdapter.notifyItemInserted(0);

            parentFragment.notifyDoneRefreshing();
        }
    }

    public void show(@Nullable Vertretungsplan vertretungsplan, ParentFragment parentFragment) {
        mVertetungsplanDetailListAdapter.notifyItemRangeRemoved(0, listItems.size());

        if (getParentFragmentManager() != null && !getParentFragmentManager().isStateSaved()) {
            if (vertretungsplan == null) {
                getParentFragmentManager()
                        .beginTransaction()
                        .hide(this)
                        .commit();
            } else {
                getParentFragmentManager()
                        .beginTransaction()
                        .show(this)
                        .commit();

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
                for (GradeItem g : vertretungsplanForDate.getGradeItems()) {
                    for (String[] a : g.getVertretungsplanItems()) {
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

                mVertetungsplanDetailListAdapter.notifyItemRangeInserted(0, listItems.size());
            }
            parentFragment.notifyDoneRefreshing();
        }
    }
}
