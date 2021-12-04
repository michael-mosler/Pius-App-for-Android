package com.rmkrings.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.adapter.EvaListAdapter;
import com.rmkrings.data.eva.Eva;
import com.rmkrings.data.eva.EvaCourseItem;
import com.rmkrings.data.eva.EvaDateItem;
import com.rmkrings.data.eva.EvaItem;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.EvaLoader;
import com.rmkrings.activities.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * EVA Fragment shows list of EVA tasks with respect to configured
 * course list.
 */
public class EvaFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private ProgressBar mProgressBar;
    private EvaListAdapter mEvaListAdapter;

    // Local State
    private final String digestFileName = Config.digestFilename("eva");
    private final String cacheFileName = Config.cacheFilename("eva");

    private final Cache cache = new Cache();
    private Eva eva;
    private final ArrayList<BaseListItem> evaList = new ArrayList<>();

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    public EvaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mEvaList = view.findViewById(R.id.evaList);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mEvaList.setLayoutManager(mVerticalLayoutManager);
        mEvaListAdapter = new EvaListAdapter(evaList);
        mEvaList.setAdapter(mEvaListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eva, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(getResources().getString(R.string.title_eva));
        reload();
    }

    /**
     * Update EVA list in EVA list adapter by recreating private evaList property.
     */
    private void setEvaList() {
        mEvaListAdapter.notifyItemRangeRemoved(0, evaList.size());
        evaList.clear();

        if (eva.getDates().size() == 0) {
            evaList.add(new MessageItem(getResources().getString(R.string.text_no_eva), Gravity.CENTER));
            mEvaListAdapter.notifyItemInserted(0);
        } else {
            for (String date : eva.getDates()) {
                evaList.add(new EvaDateItem(date));
                for (EvaItem evaItem : Objects.requireNonNull(eva.getEvaData().get(date))) {
                    evaList.add(new EvaCourseItem(evaItem.getCourse()));
                    evaList.add(new MessageItem(evaItem.getEvaText()));
                }
            }

            mEvaListAdapter.notifyItemRangeInserted(0, evaList.size());
        }
    }

    /**
     * Load EVA list from backend. Once done this.execute will be called.
     */
    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        EvaLoader evaLoader = new EvaLoader(AppDefaults.getGradeSetting());
        evaLoader.load(this, digest);

    }

    /**
     * Callback for EVA load. This method stops any load animation and eveluates data received.
     * On error an error message is shown. If loading was ok then interal cache is updated
     * and EVA list view is updated by calling this.setEvaList().
     * Any exception is ignored as we can't do much about this. In most situations it will
     * even not be possible to show another popup, most likely because no context is available
     * any longer.
     * @param responseData - Data received from backend.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        mProgressBar.setVisibility(View.INVISIBLE);

        /*
         * Non 200 HTTP status code received. Show error message and return as nothing
         * can be shown.
         */
        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Calendar. HTTP Status code %d.", responseData.getHttpStatusCode()));
            if (getActivity() != null && !getActivity().isFinishing()) {
                new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                        .setTitle(getResources().getString(R.string.title_eva))
                        .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            if (getParentFragmentManager() != null) {
                                getParentFragmentManager().popBackStack();
                            }
                        })
                        .show();
            }
            return;
        }

        /*
         * Loading was ok. Update interal cache and hash value and refresh EVA list view.
         */
        if (responseData.getData() != null) {
            data = responseData.getData();
            cache.store(cacheFileName, data);
        } else {
            data = cache.read(cacheFileName);
        }

        try {
            jsonData = new JSONObject(data);
            eva = new Eva(jsonData);

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && eva.getDigest() != null) {
                cache.store(digestFileName, eva.getDigest());
            }

            setEvaList();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
