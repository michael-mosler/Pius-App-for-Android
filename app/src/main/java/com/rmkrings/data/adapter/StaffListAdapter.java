package com.rmkrings.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.rmkrings.activities.R;
import com.rmkrings.data.staff.StaffDictionary;
import com.rmkrings.data.staff.StaffMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class StaffListAdapter extends BaseExpandableListAdapter {

    public static final String TEACHERS = "Lehrer";
    public static final String SUPPLEMENT = "Betreuung";

    private final Context context;
    private final String[] listDataHeader = { TEACHERS, SUPPLEMENT };
    private final HashMap<String, StaffDictionary> listDataChild;
    private final ArrayList<StaffMember> teachers = new ArrayList<>();
    private final ArrayList<StaffMember> supplement = new ArrayList<>();

    public StaffListAdapter(
            Context context,
            HashMap<String, StaffDictionary> listDataChild
    ) {
        this.context = context;
        this.listDataChild = listDataChild;

    }

    @Override
    public int getGroupCount() {
        return listDataHeader.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        StaffDictionary staffDictionary = listDataChild.get(listDataHeader[groupPosition]);
        if (staffDictionary != null) {
            return staffDictionary.size();
        }

        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<StaffMember> staffMembers = (groupPosition == 0)
                ? teachers
                : supplement;

        return staffMembers.get(childPosition);
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.vertretungsplan_group, parent, false);
        }

        ((TextView) convertView
                .findViewById(R.id.vertretungsplan_list_group))
                .setText((String) getGroup(groupPosition));

        convertView.setEnabled(getChildrenCount(groupPosition) > 0);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        StaffMember staffMember = (StaffMember) getChild(groupPosition, childPosition);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.staff_item, parent, false);

        ((TextView)convertView.findViewById(R.id.shortName)).setText(staffMember.getShortName());
        ((TextView)convertView.findViewById(R.id.longName)).setText(staffMember.getName());
        ((TextView)convertView.findViewById(R.id.subjects)).setText(staffMember.getSubjectsAsString());

        String email = staffMember.getEmail();
        if (email != null) {
            convertView.findViewById(R.id.email).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.email)).setText(email);
        } else {
            convertView.findViewById(R.id.email).setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void notifyDataSetChanged() {
        teachers.clear();
        supplement.clear();

        // Sort staff dictionary and add all members to array list. This allows access
        // by row index in group.
        TreeMap<String, StaffMember> sortedMap;
        sortedMap = new TreeMap<>(listDataChild.get(TEACHERS));
        sortedMap.forEach((shortName, entry) -> teachers.add(entry));

        sortedMap = new TreeMap<>(listDataChild.get(SUPPLEMENT));
        sortedMap.forEach((shortName, entry) -> supplement.add(entry));

        super.notifyDataSetChanged();
    }
}
