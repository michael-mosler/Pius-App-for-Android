package com.rmkrings.data.vertretungsplan;

import com.rmkrings.helper.StringHelper;

public class VertretungsplanEvaItem extends VertretungsplanListItem {

    private final String evaText;

    public VertretungsplanEvaItem(String evaText) {
        this.evaText = StringHelper.replaceHtmlEntities(evaText);
    }

    public String getEvaText() {
        return evaText;
    }

    @Override
    public int getType() {
        return evaItem;
    }
}
