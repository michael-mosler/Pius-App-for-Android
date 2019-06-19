package com.rmkrings.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class ScheduleChangedActivity extends AppCompatActivity {
    // Outlets
    private VertretungsplanChangesAdapter mVertretungsplanChangesAdapter;

    // Local State
    private final ArrayList<BaseListItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_changed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ScheduleChangedActivity.this, MainActivity.class);
                a.putExtra("target", MainActivity.targetDashboard);
                startActivity(a);
            }
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
                showChanges(vertretungsplanChangeList, timestamp);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
                list.add(new VertretungsplanHeaderItem(d.getCourse(), d.getLesson()));
                list.add(new VertretungsplanChangeTypeItem(d.getChangeType()));
                list.add(d.getDetailNew());
                list.add(d.getDetailOld());

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