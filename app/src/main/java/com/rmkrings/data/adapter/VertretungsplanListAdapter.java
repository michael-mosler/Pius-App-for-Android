package com.rmkrings.data.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.rmkrings.activities.R;

public class VertretungsplanListAdapter extends BaseExpandableListAdapter {

    private final Context context;

    // Header titles
    private final List<String> listDataHeader; // header titles

    // child data in format of header title, child title
    private final HashMap<String, List<String>> listDataChild;

    public VertretungsplanListAdapter(
            Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData)
    {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return Objects.requireNonNull(this.listDataChild.get(this.listDataHeader.get(groupPosition))).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(
            int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent)
    {
        final String childText = (String)getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.vertretungsplan_item, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.vertretungsplan_list_item);
        txtListChild.setText(childText);
        return convertView;
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
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(
            int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent)
    {
        String headerTitle = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.vertretungsplan_group, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.vertretungsplan_list_group);
        lblListHeader.setText(headerTitle);

        if (getChildrenCount(groupPosition) == 0) {
            convertView.setEnabled(false);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
