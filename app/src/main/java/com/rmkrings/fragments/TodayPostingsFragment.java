package com.rmkrings.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.adapter.PostingsAdapter;
import com.rmkrings.data.postings.Postings;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.interfaces.ParentFragment;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.PostingsLoader;
import com.rmkrings.activities.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;


public class TodayPostingsFragment extends Fragment implements HttpResponseCallback {

    // Outlets
    private PostingsAdapter mPostingsAdapter;

    // Local State
    private final String digestFileName = Config.digestFilename("postings");
    private final String cacheFileName = Config.cacheFilename("postings");
    private final Cache cache = new Cache();
    private Postings postings;
    private final ArrayList<BaseListItem> itemlist = new ArrayList<>();
    private ParentFragment parentFragment;

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());


    public TodayPostingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mPostings = view.findViewById(R.id.postinglist);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mPostings.setLayoutManager(mVerticalLayoutManager);
        mPostings.addItemDecoration(new DividerItemDecoration(mPostings.getContext(), DividerItemDecoration.VERTICAL));
        mPostingsAdapter = new PostingsAdapter(itemlist);
        mPostings.setAdapter(mPostingsAdapter);

        getParentFragmentManager()
                .beginTransaction()
                .hide(this)
                .commit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_postings, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        final PostingsLoader postingsLoader = new PostingsLoader();
        postingsLoader.load(this, digest);
    }

    private void show(String message, ParentFragment parentFragment) {
        mPostingsAdapter.notifyItemRangeRemoved(0, itemlist.size());
        itemlist.clear();
        itemlist.add(new MessageItem(message, Gravity.CENTER));
        mPostingsAdapter.notifyItemInserted(0);
        parentFragment.notifyDoneRefreshing();
    }

    public void show(ParentFragment parentFragment) {
        this.parentFragment = parentFragment;
        reload();
    }

    private void setPostings() {
        if (isAdded() && getParentFragmentManager() != null && !getParentFragmentManager().isStateSaved()) {
            if (postings.getPostings() != null && postings.getPostings().size() == 0) {
                getParentFragmentManager()
                        .beginTransaction()
                        .hide(this)
                        .commit();
            } else {
                getParentFragmentManager()
                        .beginTransaction()
                        .show(this)
                        .commit();
                mPostingsAdapter.notifyItemRangeRemoved(0, itemlist.size());
                itemlist.clear();
                itemlist.addAll(Objects.requireNonNull(postings.getPostings()));
                mPostingsAdapter.notifyItemRangeInserted(0, itemlist.size());
            }
            parentFragment.notifyDoneRefreshing();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Calendar. HTTP Status code %d.", responseData.getHttpStatusCode()));
            show(getResources().getString(R.string.error_failed_to_load_data), parentFragment);
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
            postings = new Postings(jsonData);

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && postings.getDigest() != null) {
                cache.store(digestFileName, postings.getDigest());
            }

            setPostings();
        }
        catch (Exception e) {
            e.printStackTrace();
            show(getResources().getString(R.string.error_failed_to_load_data), parentFragment);
        }
    }
}
