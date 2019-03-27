package com.rmkrings.data.vertretungsplan;

public class VertretungsplanRemarkItem extends VertretungsplanListItem {

    private String remarkText;

    public VertretungsplanRemarkItem(String remarkText) {
        this.remarkText = remarkText;
    }

    public String getRemarkText() {
        return remarkText;
    }

    @Override
    public int getType() {
        return remarkItem;
    }
}
