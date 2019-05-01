package com.rmkrings.data.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rmkrings.data.calendar.MonthListSelectionCallback;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;

public class CalendarMonthListAdapter extends RecyclerView.Adapter<CalendarMonthListAdapter.MonthListViewHolder> {

    private MonthListSelectionCallback fragment;
    private ArrayList<String> monthList;

    static class MonthListViewHolder extends RecyclerView.ViewHolder {
        Button buttonView;
        MonthListViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            buttonView = itemView.findViewById(R.id.button);
        }
    }

    public CalendarMonthListAdapter(ArrayList<String> monthList, MonthListSelectionCallback fragment) {
        this.fragment = fragment;
        this.monthList = monthList;
    }

    @NonNull
    @Override
    public CalendarMonthListAdapter.MonthListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout)LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.month__selector_item, viewGroup, false);
        return new MonthListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MonthListViewHolder viewHolder, int i) {
        viewHolder.buttonView.setText(monthList.get(i));

        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.notifySelectionChanged(viewHolder.buttonView, viewHolder.buttonView.getText().toString());
            }
        });

        if (i == 0) {
            fragment.notifySelectionChanged(viewHolder.buttonView, monthList.get(0));
        }
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }
}
