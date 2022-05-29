package com.rmkrings.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.widget.TextView;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.adapter.VertretungsplanChangesAdapter;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeGroupItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeList;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeTypeItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.DateHelper;
import com.rmkrings.pius_app_for_android;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ScheduleChangedActivity extends AppCompatActivity {
    // Outlets
    private VertretungsplanChangesAdapter mVertretungsplanChangesAdapter;

    // Local State
    private final ArrayList<BaseListItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_changed);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent a = new Intent(ScheduleChangedActivity.this, MainActivity.class);
            a.putExtra("target", MainActivity.getTargetDashboard());
            startActivity(a);
        });

        RecyclerView mList = findViewById(R.id.changeSet);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(pius_app_for_android.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(),
                LinearLayoutManager.VERTICAL);
        mList.addItemDecoration(dividerItemDecoration);
        mVertretungsplanChangesAdapter = new VertretungsplanChangesAdapter(list);
        mList.setAdapter(mVertretungsplanChangesAdapter);

        if (getIntent().getExtras() != null) {
            try {
                String data = getIntent().getStringExtra("deltaList");
                String timestamp = getIntent().getStringExtra("timestamp");
                VertretungsplanChangeList vertretungsplanChangeList = new VertretungsplanChangeList(new JSONArray(data));
                showChanges(vertretungsplanChangeList, Objects.requireNonNull(timestamp));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ColorDrawable bgndColor = new ColorDrawable(Color.WHITE);
            ab.setTitle(
                    Html.fromHtml(
                        "<font color='#000000'>"
                                .concat(getResources().getString(R.string.title_my_subst_schedule_changes))
                                .concat("</font>"),
                        Html.FROM_HTML_MODE_COMPACT
                    )
            );
            ab.setBackgroundDrawable(bgndColor);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showChanges(VertretungsplanChangeList vertretungsplanChangeList, String timestamp) {
        TextView mTimestamp = findViewById(R.id.timestamp);
        String readableTimestamp = DateHelper.convert(
                timestamp.replace("Z", "+00:00"), "yyyy-MM-dd'T'HH:mm:ssz", "EEEE, d. MMMM yyyy, HH:mm 'Uhr'");
        mTimestamp.setText(readableTimestamp);

        list.clear();
        for (Map.Entry<String, ArrayList<VertretungsplanChangeDetailItem>> change: vertretungsplanChangeList.getChanges().entrySet()) {
            String date = change.getKey();
            list.add(new VertretungsplanChangeGroupItem(date));

            ArrayList<VertretungsplanChangeDetailItem> a = change.getValue();
            for (VertretungsplanChangeDetailItem d: a) {
                list.add(new VertretungsplanChangeTypeItem(d.getChangeType()));
                list.add(new VertretungsplanHeaderItem(d.getCourse(), d.getLesson()));

                if (d.getDetailNew() != null) {
                    list.add(d.getDetailNew());
                }

                if (d.getDetailOld() != null) {
                    list.add(d.getDetailOld());
                }

                VertretungsplanRemarkItem r = new VertretungsplanRemarkItem(d.getRemark());
                if (r.getRemarkText().trim().length() != 0) {
                    list.add(r);
                }

                if (d.getEvaText() != null) {
                    list.add(new VertretungsplanEvaItem(d.getEvaText()));
                }
            }
        }

        mVertretungsplanChangesAdapter.notifyDataSetChanged();
    }
}
