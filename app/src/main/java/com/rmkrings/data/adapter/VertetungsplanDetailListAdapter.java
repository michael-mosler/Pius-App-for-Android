package com.rmkrings.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.pius_app_for_android.R;

/**
 * Adapter for Vertretungsplan detailed list. It expects a list of VertretungsplanItems for
 * a certain grade and date. It displays all items from the list.
 */
public class VertetungsplanDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<VertretungsplanListItem> list;

    /**
     * TextView view holder: Holds data for items that contain a single TextView.
     */
    public static class TextViewViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextViewViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    /**
     * Holds information for DetailItem list entries.
     */
    public static class DetailItemViewHolder extends RecyclerView.ViewHolder {
        public TextView substitutionType;
        public TextView room;
        public TextView teacher;
        public DetailItemViewHolder(LinearLayout l) {
            super(l);
            substitutionType = l.findViewById(R.id.substitutionType);
            room = l.findViewById(R.id.room);
            teacher = l.findViewById(R.id.teacher);
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
                vh = new TextViewViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.detailItem: {
                LinearLayout itemView = (LinearLayout)mLayoutInflater.inflate(R.layout.veretretungsplan_detail_item, parent, false);
                vh = new DetailItemViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.remarkItem: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, parent, false);
                vh = new TextViewViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.evaItem: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_eva_item, parent, false);
                vh = new TextViewViewHolder(itemView);
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
                TextViewViewHolder viewHolder = (TextViewViewHolder)holder;
                viewHolder.textView.setText(
                        (headerItem.getCourse().length() > 0)
                        ? String.format("Fach/Kurs: %s, %s. Stunde", headerItem.getCourse(), headerItem.getLesson())
                        : String.format("%s. Stunde", headerItem.getLesson()));
                break;
            }

            case VertretungsplanListItem.detailItem: {
                VertretungsplanDetailItem detailItem = (VertretungsplanDetailItem)list.get(position);
                DetailItemViewHolder viewHolder = (DetailItemViewHolder)holder;

                viewHolder.substitutionType.setText(detailItem.getSubstitutionType());

                viewHolder.room.setText(detailItem.getRoom(), TextView.BufferType.SPANNABLE);
                FormatHelper.roomText(viewHolder.room);

                viewHolder.teacher.setText(detailItem.getTeacher());
                break;
            }

            case VertretungsplanListItem.remarkItem: {
                VertretungsplanRemarkItem remarkItem = (VertretungsplanRemarkItem)list.get(position);
                TextViewViewHolder viewHolder = (TextViewViewHolder)holder;
                viewHolder.textView.setText(remarkItem.getRemarkText());
                break;
            }

            case VertretungsplanListItem.evaItem: {
                VertretungsplanEvaItem evaItem = (VertretungsplanEvaItem)list.get(position);
                TextViewViewHolder viewHolder = (TextViewViewHolder)holder;
                viewHolder.textView.setText(evaItem.getEvaText());
                break;
            }
        }
    }
}