package com.rmkrings.data.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rmkrings.activities.R;
import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeGroupItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanChangeTypeItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.helper.StringHelper;
import com.rmkrings.pius_app_for_android;

import java.util.ArrayList;

public class VertretungsplanChangesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
        DetailItemViewHolder(LinearLayout l) {
            super(l);
            substitutionType = l.findViewById(R.id.widgetSubstitutionTypeItem);
            room = l.findViewById(R.id.widgetRoomItem);
            teacher = l.findViewById(R.id.widgetTeacherItem);
        }
    }

    public VertretungsplanChangesAdapter(ArrayList<BaseListItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case VertretungsplanChangeListItem.groupItem:
            case VertretungsplanChangeListItem.courseHeader: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_header_item, parent, false);
                vh = new VertretungsplanChangesAdapter.TextViewHolder(itemView);
                break;
            }

            case VertretungsplanChangeListItem.changeTypeItem:
            case VertretungsplanChangeListItem.remarkItem: {
                TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, parent, false);
                vh = new VertretungsplanChangesAdapter.TextViewHolder(itemView);
                break;
            }

            case VertretungsplanChangeListItem.detailItem: {
                LinearLayout itemView = (LinearLayout)mLayoutInflater.inflate(R.layout.veretretungsplan_detail_item, parent, false);
                vh = new VertretungsplanChangesAdapter.DetailItemViewHolder(itemView);
                break;
            }

            default: { // EVA Item
                TextView itemView = (TextView) mLayoutInflater.inflate(R.layout.vertretungsplan_eva_item, parent, false);
                vh = new VertretungsplanChangesAdapter.TextViewHolder(itemView);
            }
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int type = getItemViewType(position);

        switch(type) {
            case VertretungsplanChangeListItem.groupItem: {
                VertretungsplanChangeGroupItem groupItem = (VertretungsplanChangeGroupItem)list.get(position);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setText(groupItem.getDate());
                break;
            }

            case VertretungsplanChangeListItem.courseHeader: {
                VertretungsplanHeaderItem headerItem = (VertretungsplanHeaderItem)list.get(position);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setText(
                        (headerItem.getCourse().length() > 0)
                                ? String.format("Fach/Kurs: %s, %s. Stunde", StringHelper.replaceHtmlEntities(headerItem.getCourse()), headerItem.getLesson())
                                : String.format("%s. Stunde", headerItem.getLesson()));
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setBackgroundColor(pius_app_for_android.getAppContext().getResources().getColor(R.color.colorPiusLightBlue));
                break;
            }

            case VertretungsplanChangeListItem.changeTypeItem: {
                VertretungsplanChangeTypeItem changeTypeItem = (VertretungsplanChangeTypeItem)list.get(position);

                String readableChangeType;
                int bgndColor;
                int fgndColor;
                switch(changeTypeItem.getChangeType()) {
                    case "CHANGED": {
                        bgndColor = pius_app_for_android.getAppContext().getResources().getColor(R.color.colorChanged);
                        fgndColor = Color.BLACK;
                        readableChangeType = pius_app_for_android.getAppContext().getResources().getString(R.string.label_changed);
                        break;
                    }

                    case "DELETED": {
                        bgndColor = pius_app_for_android.getAppContext().getResources().getColor(R.color.colorDeleted);
                        fgndColor = Color.WHITE;
                        readableChangeType = pius_app_for_android.getAppContext().getResources().getString(R.string.label_deleted);
                        break;
                    }

                    default: {
                        bgndColor = pius_app_for_android.getAppContext().getResources().getColor(R.color.colorAdded);
                        fgndColor = Color.BLACK;
                        readableChangeType = pius_app_for_android.getAppContext().getResources().getString(R.string.label_added);
                    }
                }

                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setText(readableChangeType);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setTextColor(fgndColor);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setBackgroundColor(bgndColor);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setGravity(Gravity.CENTER);
                break;
            }

            case VertretungsplanChangeListItem.detailItem: {
                VertretungsplanDetailItem detailItem = (VertretungsplanDetailItem)list.get(position);
                VertretungsplanChangesAdapter.DetailItemViewHolder detailItemViewHolder = (VertretungsplanChangesAdapter.DetailItemViewHolder)holder;

                detailItemViewHolder.substitutionType.setText(detailItem.getSubstitutionType());
                detailItemViewHolder.room.setText(detailItem.getRoom(), TextView.BufferType.SPANNABLE);
                FormatHelper.roomText(detailItemViewHolder.room);
                detailItemViewHolder.teacher.setText(detailItem.getTeacher());

                if (detailItem.isOutdated()) {
                    detailItemViewHolder.substitutionType.setTextColor(Color.GRAY);
                    detailItemViewHolder.room.setTextColor(Color.GRAY);
                    detailItemViewHolder.teacher.setTextColor(Color.GRAY);
                }
                break;
            }

            case VertretungsplanChangeListItem.remarkItem: {
                VertretungsplanRemarkItem remarkItem = (VertretungsplanRemarkItem)list.get(position);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setText(remarkItem.getRemarkText());
                break;
            }

            case VertretungsplanChangeListItem.evaItem: {
                VertretungsplanEvaItem evaItem = (VertretungsplanEvaItem)list.get(position);
                ((VertretungsplanChangesAdapter.TextViewHolder)holder).textView.setText(evaItem.getEvaText());
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
