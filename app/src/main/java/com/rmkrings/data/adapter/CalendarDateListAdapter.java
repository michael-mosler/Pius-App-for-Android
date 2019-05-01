package com.rmkrings.data.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.calendar.DayItem;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;

public class CalendarDateListAdapter extends RecyclerView.Adapter<CalendarDateListAdapter.DateListViewHolder> {

    private ArrayList<DayItem> dateList;

    static class DateListViewHolder extends RecyclerView.ViewHolder {

        public TextView dateView;
        public TextView eventView;
        public DateListViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date);
            eventView = itemView.findViewById(R.id.event);
        }
    }

    public CalendarDateListAdapter(ArrayList<DayItem> dateList) {
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public DateListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout)LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.date_list_item, viewGroup, false);
        return new DateListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateListViewHolder dateListViewHolder, int i) {
        DayItem dayItem = dateList.get(i);
        dateListViewHolder.dateView.setText(dayItem.getDay());
        dateListViewHolder.eventView.setText(dayItem.getEvent());
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
