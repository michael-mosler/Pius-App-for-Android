package com.rmkrings.PiusApp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.main.PiusApp;
import com.rmkrings.pius_app_for_android.R;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VertretungsplanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VertretungsplanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VertretungsplanFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private TextView mLastUpdate;
    private ExpandableListView mVertretungsplanListView;
    private VertretungsplanListAdapter mVertretunsplanListAdapter;

    // Listeners
    private OnFragmentInteractionListener mListener;

    // Local state.
    private Vertretungsplan vertretungsplan;
    private String[] metaData = new String[2];
    private ArrayList<String> listDataHeader = new ArrayList<>(0);
    private HashMap<String, List<String>> listDataChild = new HashMap<>(0);
    private FragmentActivity fragmentActivity;

    public VertretungsplanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment VertretungsplanFragment.
     */
    public static VertretungsplanFragment newInstance() {
        VertretungsplanFragment fragment = new VertretungsplanFragment();
        fragment.setArguments(null);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mMetaData = view.findViewById(R.id.metadata);
        mLastUpdate = view.findViewById(R.id.lastupdate);
        mVertretungsplanListView = view.findViewById(R.id.vertretungsplanListView);

        mMetaData.setHasFixedSize(true);

        // Create Meta Data output widgets.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PiusApp.getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        mMetaData.setLayoutManager(mLayoutManager);
        mAdapter = new MetaDataAdapter(metaData);
        mMetaData.setAdapter(mAdapter);

        // preparing list data
        mVertretunsplanListAdapter = new VertretungsplanListAdapter(PiusApp.getAppContext(), listDataHeader, listDataChild);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vertretungsplan, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity)context;

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
        getActivity().setTitle(R.string.title_substitution_schedule);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        mProgressBar.setVisibility(View.VISIBLE);
        VertretungsplanLoader vertretungsplanLoader = new VertretungsplanLoader(null);
        vertretungsplanLoader.load(this);
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
    }

    @Override
    public void execute(HttpResponseData responseData) {
        String data = null;
        JSONObject jsonData;

        mProgressBar.setVisibility(View.INVISIBLE);

        // @TODO Error Handling
        // @TODO Read from cache

        if (responseData.getData() != null) {
            data = responseData.getData();
        } else {
            // @TODO No error and no data: We need to read data from cache.
        }

        try {
            jsonData = new JSONObject(data);
            vertretungsplan = new Vertretungsplan(jsonData);
            setMetaData();
            setLastUpdate();
            setVertretungsplanList();
        } catch (Exception e) {
            e.printStackTrace();
            // @TODO Show error and return.
        }
    }

    /**
     *
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener { }
}
