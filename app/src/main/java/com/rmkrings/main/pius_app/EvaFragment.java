package com.rmkrings.main.pius_app;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.EvaLoader;
import com.rmkrings.pius_app_for_android.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * A simple {@link Fragment} subclass.
 */
public class EvaFragment extends Fragment implements HttpResponseCallback {
    // Outlets
    private ProgressBar mProgressBar;
    private EvaListAdapter mEvaListAdapter;

    // Local State
    private final String digestFileName = "eva.md5";
    private final String cacheFileName = "eva.json";

    private final Cache cache = new Cache();
    private Eva eva;
    private final ArrayList<BaseListItem> evaList = new ArrayList<>();

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    public EvaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.progressBar);
        RecyclerView mEvaList = view.findViewById(R.id.evaList);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);
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
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.title_eva));
        reload();
    }

    private void setEvaList() {
        evaList.clear();

        if (eva.getDates().size() == 0) {
            evaList.add(new MessageItem(getResources().getString(R.string.text_no_eva), Gravity.CENTER));
        } else {
            for (String date : eva.getDates()) {
                evaList.add(new EvaDateItem(date));
                for (EvaItem evaItem : Objects.requireNonNull(eva.getEvaData().get(date))) {
                    evaList.add(new EvaCourseItem(evaItem.getCourse()));
                    evaList.add(new MessageItem(evaItem.getEvaText()));
                }
            }
        }

        mEvaListAdapter.notifyDataSetChanged();
    }

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

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        mProgressBar.setVisibility(View.INVISIBLE);

        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Calendar. HTTP Status code %d.", responseData.getHttpStatusCode()));
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_calendar))
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