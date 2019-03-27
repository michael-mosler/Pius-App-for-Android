package com.rmkrings.data.vertretungsplan;

public class VertretungsplanDetailItem extends VertretungsplanListItem {

    private String substitutionType;
    private String room;
    private String teacher;


    public VertretungsplanDetailItem(String substitutionType, String room, String teacher) {
        this.substitutionType = substitutionType;
        this.room = room;
        this.teacher = teacher;
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
