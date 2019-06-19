package com.rmkrings.data.vertretungsplan;

import com.rmkrings.helper.StringHelper;

public class VertretungsplanDetailItem extends VertretungsplanListItem {

    private final String substitutionType;
    private final String room;
    private final String teacher;
    private final Boolean outdated;

    public VertretungsplanDetailItem(String substitutionType, String room, String teacher) {
        this.substitutionType = StringHelper.replaceHtmlEntities(substitutionType);
        this.room = StringHelper.replaceHtmlEntities(room);
        this.teacher = StringHelper.replaceHtmlEntities(teacher);
        this.outdated = false;
    }

    @SuppressWarnings("SameParameterValue")
    VertretungsplanDetailItem(String substitutionType, String room, String teacher, Boolean outdated) {
        this.substitutionType = StringHelper.replaceHtmlEntities(substitutionType);
        this.room = StringHelper.replaceHtmlEntities(room);
        this.teacher = StringHelper.replaceHtmlEntities(teacher);
        this.outdated = outdated;
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

    public Boolean isOutdated() {
        return outdated;
    }

    @Override
    public int getType() {
        return detailItem;
    }
}
