package com.rmkrings.PiusApp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.adapter.MetaDataAdapter;
import com.rmkrings.data.adapter.VertetungsplanDetailListAdapter;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.main.PiusApp;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VertretungsplanDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VertretungsplanDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VertretungsplanDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "gradeItem";
    private static final String ARG_PARAM2 = "date";

    // Outlets
    private TextView mDate;
    private RecyclerView.Adapter mAdapter;

    // Local State
    private GradeItem gradeItem;
    private String date;
    private ArrayList<VertretungsplanListItem> list = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView mList = view.findViewById(R.id.detaillist);
        mDate = view.findViewById(R.id.date);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PiusApp.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(mLayoutManager);
        mAdapter = new VertetungsplanDetailListAdapter(list);
        mList.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vertretungsplan_detail, container, false);
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(gradeItem.getGrade());
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        list = prepareVertretungsplanItems(list);

        mDate.setText(date);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Convert vetretungsplan items from grade item into linear list that represents
     * table content.
     * @return
     */
    private ArrayList<VertretungsplanListItem> prepareVertretungsplanItems(ArrayList<VertretungsplanListItem> list) {
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

        return list;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
