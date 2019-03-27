package com.rmkrings.data.vertretungsplan;

public class VertretungsplanEvaItem extends VertretungsplanListItem {

    private String evaText;

    public VertretungsplanEvaItem(String evaText) {
        this.evaText = evaText;
    }

    public String getEvaText() {
        return evaText;
    }

    @Override
    public int getType() {
        return evaItem;
    }
}
