package com.rmkrings.data.vertretungsplan;

import com.rmkrings.helper.StringHelper;

public class VertretungsplanDetailItem extends VertretungsplanListItem {

    private String substitutionType;
    private String room;
    private String teacher;


    public VertretungsplanDetailItem(String substitutionType, String room, String teacher) {
        this.substitutionType = StringHelper.replaceHtmlEntities(substitutionType);
        this.room = StringHelper.replaceHtmlEntities(room);
        this.teacher = StringHelper.replaceHtmlEntities(teacher);
    }

    public String getSubstitutionType() {
        return substitutionType;
    }

    public String getRoom() {
        return room;
    }

    public String getTeacher() {
        return teacher;
    }

    @Override
    public int getType() {
        return detailItem;
    }

}
