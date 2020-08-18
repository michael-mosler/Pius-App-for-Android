package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.eva.EvaCourseItem;
import com.rmkrings.data.eva.EvaDateItem;
import com.rmkrings.data.eva.EvaListItem;
import com.rmkrings.pius_app_for_android;
import com.rmkrings.activities.R;

import java.util.ArrayList;

/**
 * Shows history of EVA tasks. The class simply implements standard list view interface
 * messages. Constructor expects the list of EVA items that shall be displayed.
 */
public class EvaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<BaseListItem> list;

    /**
     * View holder for a single EVA task. TextView is set selectable.
     */
    static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        TextViewHolder(@NonNull TextView v) {
            super(v);
            v.setTextIsSelectable(true);
            textView = v;
        }
    }

    public EvaListAdapter(ArrayList<BaseListItem> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());

        // Depending on viewType received one of DateItem, CourseItem of text view holder is
        // created.
        switch (viewType) {
            case EvaListItem.evaDateItem:
            case EvaListItem.evaCourseItem: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_header_item, parent, false);
                vh = new EvaListAdapter.TextViewHolder(itemView);
                break;
            }

            default: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, parent, false);
                vh = new EvaListAdapter.TextViewHolder(itemView);
                break;
            }
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch(type) {
            case EvaListItem.evaDateItem: {
                EvaDateItem evaDateItem = (EvaDateItem)list.get(position);
                ((TextViewHolder)holder).textView.setText(evaDateItem.getDate());

                RecyclerView.LayoutParams parameter = (RecyclerView.LayoutParams)((TextViewHolder)holder).textView.getLayoutParams();
                parameter.setMargins(parameter.leftMargin, parameter.topMargin, parameter.rightMargin, 4);
                ((TextViewHolder)holder).textView.setLayoutParams(parameter);
                break;
            }

            case EvaListItem.evaCourseItem: {
                EvaCourseItem evaCourseItem = (EvaCourseItem)list.get(position);
                ((TextViewHolder)holder).textView.setText(evaCourseItem.getCourse());
                ((TextViewHolder)holder).textView.setBackgroundColor(pius_app_for_android.getAppContext().getResources().getColor(R.color.colorPiusLightBlue));
                break;
            }

            case EvaListItem.message: {
                MessageItem evaTextItem = (MessageItem)list.get(position);
                ((TextViewHolder)holder).textView.setText(evaTextItem.getMessageText());
                ((TextViewHolder)holder).textView.setGravity(evaTextItem.getGravity());

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
