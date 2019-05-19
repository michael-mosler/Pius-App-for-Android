package com.rmkrings.data.eva;

public class EvaCourseItem extends EvaListItem {

    private String course;

    public EvaCourseItem(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    @Override
    public int getType() {
        return evaCourseItem;
    }
}
