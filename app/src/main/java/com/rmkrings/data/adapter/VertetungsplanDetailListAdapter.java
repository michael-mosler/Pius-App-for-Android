package com.rmkrings.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanListItem;
import com.rmkrings.pius_app_for_android.R;

public class VertetungsplanDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<VertretungsplanListItem> list;

    /**
     * Header view holder: Here this holds information on course and lesson within a single
     * text view.
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public HeaderViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }
    public VertetungsplanDetailListAdapter(ArrayList<VertretungsplanListItem> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;

        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case VertretungsplanListItem.courseHeader: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_header_item, parent, false);
                vh = new HeaderViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.detailItem: {
                // View itemView = mLayoutInflater.inflate(R.layout.view_list_item_header, parent, false);
                // vh = new DetailItemViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.remarkItem: {
                // View itemView = mLayoutInflater.inflate(R.layout.view_list_item_header, parent, false);
                // vh = new RemarkItemViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.evaItem: {
                // View itemView = mLayoutInflater.inflate(R.layout.view_list_item_header, parent, false);
                // vh = new EvaItemViewHolder(itemView);
                break;
            }
        }

        return vh;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch(type) {
            case VertretungsplanListItem.courseHeader: {
                VertretungsplanHeaderItem headerItem = (VertretungsplanHeaderItem)list.get(position);
                HeaderViewHolder viewHolder = (HeaderViewHolder)holder;
                viewHolder.textView.setText(String.format("Fach/Kurs: %s, %s. Stunde", headerItem.getCourse(), headerItem.getLesson()));
                break;
            }

            case VertretungsplanListItem.detailItem: {
                break;
            }

            case VertretungsplanListItem.remarkItem: {
                break;
            }

            case VertretungsplanListItem.evaItem: {
                break;
            }
        }
    }

}

