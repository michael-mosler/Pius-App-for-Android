package com.rmkrings.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.adapter.VertetungsplanDetailListAdapter;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.activities.R;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.*;

public class VertretungsplanDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "gradeItem";
    private static final String ARG_PARAM2 = "date";

    // Outlets
    private TextView mDate;
    private Adapter<?> mAdapter;

    // Local State
    private GradeItem gradeItem;
    private String date;
    private final ArrayList<BaseListItem> list = new ArrayList<>();

    public VertretungsplanDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gradeItem Parameter 1.
     * @param date Parameter 2.
     * @return A new instance of fragment VertretungsplanDetailFragment.
     */
    public static VertretungsplanDetailFragment newInstance(GradeItem gradeItem, String date) {
        VertretungsplanDetailFragment fragment = new VertretungsplanDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, gradeItem);
        args.putString(ARG_PARAM2, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gradeItem = (GradeItem)getArguments().getSerializable(ARG_PARAM1);
            date = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView mList = view.findViewById(R.id.detaillist);
        mDate = view.findViewById(R.id.date);

        LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(mLayoutManager);
        mAdapter = new VertetungsplanDetailListAdapter(list);
        mList.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vertretungsplan_detail, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(gradeItem.getGrade());
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        mAdapter.notifyItemRangeRemoved(0, list.size());
        prepareVertretungsplanItems(list);

        mDate.setText(date);
        mAdapter.notifyItemRangeInserted(0, list.size());
    }

    /**
     * Convert vetretungsplan items from grade item into linear list that represents
     * table content.
     * @param list - Converts substitution elements from gradeItem property into a list of base list items.
     */
    private void prepareVertretungsplanItems(ArrayList<BaseListItem> list) {
        list.clear();

        for (String[] a: gradeItem.getVertretungsplanItems()) {
            VertretungsplanHeaderItem headerItem = new VertretungsplanHeaderItem(a[2], a[0]);
            VertretungsplanDetailItem detailItem = new VertretungsplanDetailItem(a[1], a[3], a[4]);
            VertretungsplanRemarkItem remarkItem = new VertretungsplanRemarkItem(a[6]);

            list.add(headerItem);
            list.add(detailItem);

            if (remarkItem.getRemarkText().length() > 0) {
                list.add(remarkItem);
            }

            if (a.length > 7) {
                VertretungsplanEvaItem evaItem = new VertretungsplanEvaItem(a[7]);
                list.add(evaItem);
            }
        }
    }
}
