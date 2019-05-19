package com.rmkrings.data.eva;

public class EvaDateItem extends EvaListItem {

    private String date;

    public EvaDateItem(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int getType() {
        return evaDateItem;
    }
}
