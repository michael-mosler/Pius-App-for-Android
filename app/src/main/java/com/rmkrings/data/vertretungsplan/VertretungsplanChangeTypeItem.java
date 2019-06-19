package com.rmkrings.data.vertretungsplan;

public class VertretungsplanChangeTypeItem extends VertretungsplanChangeListItem {

    private final String changeType;

    public VertretungsplanChangeTypeItem(String changeType) {
        this.changeType = changeType;
    }

    public String getChangeType() {
        return changeType;
    }

    @Override
    public int getType() {
        return changeTypeItem;
    }
}
