package com.rmkrings.data.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.rmkrings.data.vertretungsplan.VertretungsplanDetailItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanEvaItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanHeaderItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanListItem;
import com.rmkrings.data.vertretungsplan.VertretungsplanRemarkItem;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.helper.StringHelper;
import com.rmkrings.main.pius_app.PiusApplication;
import com.rmkrings.pius_app_for_android.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DashboardListAdapter extends BaseExpandableListAdapter {

    private final Context context;

    // Header titles
    private final List<String> listDataHeader; // header titles

    // child data in format of header title, child title
    private final HashMap<String, List<VertretungsplanListItem>> listDataChild;

    public DashboardListAdapter(
            Context context,
            List<String> listDataHeader,
            HashMap<String, List<VertretungsplanListItem>> listChildData)
    {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(listDataChild.get(listDataHeader.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(listDataChild.get(listDataHeader.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.vertretungsplan_group, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.vertretungsplan_list_group);
        lblListHeader.setText(headerTitle);

        convertView.setEnabled(getChildrenCount(groupPosition) > 0);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        VertretungsplanListItem vertretungsplanListItem = (VertretungsplanListItem)getChild(groupPosition, childPosition);

        switch(vertretungsplanListItem.getType()) {
            case VertretungsplanListItem.courseHeader: {
                VertretungsplanHeaderItem headerItem = (VertretungsplanHeaderItem)vertretungsplanListItem;
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.vertretungsplan_header_item, null);

                TextView tv = (TextView)convertView;
                tv.setText(
                        (headerItem.getCourse().length() > 0)
                                ? String.format("Fach/Kurs: %s, %s. Stunde", StringHelper.replaceHtmlEntities(headerItem.getCourse()), headerItem.getLesson())
                                : String.format("%s. Stunde", headerItem.getLesson()));
                tv.setBackgroundColor(PiusApplication.getAppContext().getResources().getColor(R.color.colorPiusLightBlue));
                break;
            }

            case VertretungsplanListItem.detailItem: {
                TextView tv;
                VertretungsplanDetailItem detailItem = (VertretungsplanDetailItem)vertretungsplanListItem;
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.veretretungsplan_detail_item, null);

                tv = convertView.findViewById(R.id.substitutionType);
                tv.setText(detailItem.getSubstitutionType());

                tv = convertView.findViewById(R.id.room);
                tv.setText(detailItem.getRoom(), TextView.BufferType.SPANNABLE);
                FormatHelper.roomText(tv);

                tv = convertView.findViewById(R.id.teacher);
                tv.setText(detailItem.getTeacher());
                break;
            }

            case VertretungsplanListItem.remarkItem: {
                VertretungsplanRemarkItem remarkItem = (VertretungsplanRemarkItem)vertretungsplanListItem;
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.vertretungsplan_remark_item, null);

                TextView tv = (TextView)convertView;
                tv.setText(remarkItem.getRemarkText());
                break;
            }

            case VertretungsplanListItem.evaItem: {
                VertretungsplanEvaItem evaItem = (VertretungsplanEvaItem)vertretungsplanListItem;
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.vertretungsplan_eva_item, null);

                TextView tv = (TextView)convertView;
                tv.setText(evaItem.getEvaText());
                break;
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
