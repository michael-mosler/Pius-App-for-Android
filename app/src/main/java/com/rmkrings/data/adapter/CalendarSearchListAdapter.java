package com.rmkrings.data.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.calendar.CalendarListItem;
import com.rmkrings.data.calendar.CalendarMessage;
import com.rmkrings.data.calendar.DayItem;
import com.rmkrings.data.calendar.MonthHeaderItem;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;

public class CalendarSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CalendarListItem> dateList;

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    static class DateListViewHolder extends RecyclerView.ViewHolder {

        TextView dateView;
        TextView eventView;
        DateListViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date);
            eventView = itemView.findViewById(R.id.event);
        }
    }

    public CalendarSearchListAdapter(ArrayList<CalendarListItem> dateList) {
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;

        LayoutInflater mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch(i) {
            case CalendarListItem.monthHeader: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_header_item, viewGroup, false);
                vh = new TextViewHolder(itemView);
                break;
            }

            case CalendarListItem.dayItem: {
                ConstraintLayout v = (ConstraintLayout)mLayoutInflater.inflate(R.layout.date_list_item, viewGroup, false);
                vh = new DateListViewHolder(v);
                break;
            }

            default: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, viewGroup, false);
                vh = new TextViewHolder(itemView);
                break;
            }
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);

        switch(type) {
            case CalendarListItem.monthHeader: {
                MonthHeaderItem monthHeaderItem = (MonthHeaderItem)dateList.get(i);
                ((TextViewHolder)viewHolder).textView.setText(monthHeaderItem.getMonthName());
                break;
            }

            case CalendarListItem.dayItem: {
                DayItem dayItem = (DayItem)dateList.get(i);
                DateListViewHolder dateListViewHolder = (DateListViewHolder)viewHolder;
                dateListViewHolder.dateView.setText(dayItem.getDay());
                dateListViewHolder.eventView.setText(dayItem.getEvent(), TextView.BufferType.SPANNABLE);
                FormatHelper.highlight(dateListViewHolder.eventView, dayItem.getSearchMatches());
                break;
            }

            case CalendarListItem.message: {
                CalendarMessage calendarMessage = (CalendarMessage)dateList.get(i);
                ((TextViewHolder)viewHolder).textView.setGravity(calendarMessage.getGravity());
                ((TextViewHolder)viewHolder).textView.setText(calendarMessage.getMessageText());
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dateList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
