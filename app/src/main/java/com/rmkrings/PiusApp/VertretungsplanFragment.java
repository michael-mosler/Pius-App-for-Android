package com.rmkrings.PiusApp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.adapter.MetaDataAdapter;
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.main.PiusApp;
import com.rmkrings.pius_app_for_android.R;
import com.rmkrings.vertretungsplandata.VertretungsplanLoader;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VertretungsplanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VertretungsplanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VertretungsplanFragment extends Fragment implements HttpResponseCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Outlets
    private RecyclerView mMetaData;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mLastUpdate;

    // Listeners
    private OnFragmentInteractionListener mListener;

    // Local state.
    private JSONObject jsonData;
    private String[] metaData = new String[2];

    public VertretungsplanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VertretungsplanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VertretungsplanFragment newInstance(String param1, String param2) {
        VertretungsplanFragment fragment = new VertretungsplanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMetaData = view.findViewById(R.id.metadata);
        mLastUpdate = view.findViewById(R.id.lastupdate);

        mMetaData.setHasFixedSize(true);

        // Create Meta Data output widgets.
        mLayoutManager = new LinearLayoutManager(PiusApp.getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        mMetaData.setLayoutManager(mLayoutManager);
        mAdapter = new MetaDataAdapter(metaData);
        mMetaData.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vertretungsplan, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

        VertretungsplanLoader vertretungsplanLoader = new VertretungsplanLoader();
        vertretungsplanLoader.load(this);
    }

    private void setMetaData(JSONObject data) {
        String tickerText;
        String additionalText;

        try {
            tickerText = data.getString("tickerText");
        } catch (JSONException e1) {
            tickerText = null;
        }

        try {
            additionalText = data.getString("_additionalText");
        } catch (JSONException e1) {
            additionalText = null;
        }

        this.metaData[0] = tickerText;
        this.metaData[1] = additionalText;
        mAdapter.notifyDataSetChanged();
    }

    private void setLastUpdate(JSONObject data) {
        String lastUpdate;

        try {
            lastUpdate = data.getString("lastUpdate");
            mLastUpdate.setText(lastUpdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(HttpResponseData responseData) {
        String data = null;

        // @TODO Error Handling
        if (responseData.getData() != null) {
            data = responseData.getData();
        }

        if (data != null) {
            try {
                // @TODO Convert into internal structure.
                jsonData = new JSONObject(responseData.getData());
                System.out.println(responseData.getData());
                setMetaData(jsonData);
                setLastUpdate(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
                jsonData = null;
            }

            if (jsonData != null) {

            } else {
                // @TODO Error Handling
            }
        }
    }

    /**
     *
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
