package com.rmkrings.data.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.helper.StringHelper;
import com.rmkrings.activities.R;
import com.rmkrings.layouts.StaffPopover;

/**
 * Adapter for Vertretungsplan detailed list. It expects a list of VertretungsplanItems for
 * a certain grade and date. It displays all items from the list.
 */
public class VertetungsplanDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<BaseListItem> list;

    /**
     * TextView view holder: Holds data for items that contain a single TextView.
     */
    static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        TextViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    /**
     * Holds information for DetailItem list entries.
     */
    static class DetailItemViewHolder extends RecyclerView.ViewHolder {
        final TextView substitutionType;
        final TextView room;
        final TextView teacher;

        private String teacherShortcutName;

        DetailItemViewHolder(final Context context, LinearLayout l) {
            super(l);
            substitutionType = l.findViewById(R.id.widgetSubstitutionTypeItem);
            room = l.findViewById(R.id.widgetRoomItem);
            teacher = l.findViewById(R.id.widgetTeacherItem);
            teacher.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        StaffPopover staffPopover = new StaffPopover(context, teacher, teacherShortcutName);
                        staffPopover.show();
                        return false;
                    }
                });
        }

        public void setTeacherShortcutName(String teacherShortcutName) {
            this.teacherShortcutName = teacherShortcutName;
            teacher.setText(teacherShortcutName);
        }
    }

    public VertetungsplanDetailListAdapter(ArrayList<BaseListItem> list) {
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case VertretungsplanListItem.courseHeader: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_header_item, parent, false);
                vh = new TextViewHolder(itemView);
                break;
            }

            case VertretungsplanListItem.detailItem: {
                LinearLayout itemView = (LinearLayout)mLayoutInflater.inflate(R.layout.veretretungsplan_detail_item, parent, false);
                vh = new DetailItemViewHolder(parent.getContext(), itemView);
                break;
            }

            case VertretungsplanListItem.remarkItem:
            case VertretungsplanListItem.message: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, parent, false);
                vh = new TextViewHolder(itemView);
                break;
            }

            default: { // EVA item
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_eva_item, parent, false);
                vh = new TextViewHolder(itemView);
                break;
            }
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final int type = getItemViewType(position);

        switch(type) {
            case VertretungsplanListItem.courseHeader: {
                VertretungsplanHeaderItem headerItem = (VertretungsplanHeaderItem)list.get(position);
                ((TextViewHolder)viewHolder).textView.setText(
                        (headerItem.getCourse().length() > 0)
                                ? String.format("Fach/Kurs: %s, %s. Stunde", StringHelper.replaceHtmlEntities(headerItem.getCourse()), headerItem.getLesson())
                                : String.format("%s. Stunde", headerItem.getLesson()));
                break;
            }

            case VertretungsplanListItem.detailItem: {
                VertretungsplanDetailItem detailItem = (VertretungsplanDetailItem)list.get(position);
                DetailItemViewHolder detailItemViewHolder = (DetailItemViewHolder)viewHolder;

                detailItemViewHolder.substitutionType.setText(detailItem.getSubstitutionType());
                detailItemViewHolder.room.setText(detailItem.getRoom(), TextView.BufferType.SPANNABLE);
                FormatHelper.roomText(detailItemViewHolder.room);
                // detailItemViewHolder.teacher.setText(detailItem.getTeacher());
                detailItemViewHolder.setTeacherShortcutName(detailItem.getTeacher());
                break;
            }

            case VertretungsplanListItem.remarkItem: {
                VertretungsplanRemarkItem remarkItem = (VertretungsplanRemarkItem)list.get(position);
                ((TextViewHolder)viewHolder).textView.setText(remarkItem.getRemarkText());
                break;
            }

            case VertretungsplanListItem.evaItem: {
                VertretungsplanEvaItem evaItem = (VertretungsplanEvaItem)list.get(position);
                ((TextViewHolder)viewHolder).textView.setText(evaItem.getEvaText());
                break;
            }

            case VertretungsplanListItem.message: {
                MessageItem messageItem = (MessageItem)list.get(position);
                ((TextViewHolder)viewHolder).textView.setGravity(messageItem.getGravity());
                ((TextViewHolder)viewHolder).textView.setText(messageItem.getMessageText());
                break;
            }
        }
    }
}