package com.rmkrings.data.vertretungsplan;

public class VertretungsplanChangeGroupItem extends VertretungsplanChangeListItem {

    private final String date;

    public VertretungsplanChangeGroupItem(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int getType() {
        return groupItem;
    }
}
