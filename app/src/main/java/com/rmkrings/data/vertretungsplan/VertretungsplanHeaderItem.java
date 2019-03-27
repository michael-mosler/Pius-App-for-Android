package com.rmkrings.data.vertretungsplan;

public class VertretungsplanHeaderItem extends VertretungsplanListItem {

    private String course;
    private String lesson;

    public VertretungsplanHeaderItem(String course, String lesson) {
        this.course = course;
        this.lesson = lesson;
    }

    public String getCourse() {
        return course;
    }

    public String getLesson() {
        return lesson;
    }

    @Override
    public int getType() {
        return courseHeader;
    }
}
