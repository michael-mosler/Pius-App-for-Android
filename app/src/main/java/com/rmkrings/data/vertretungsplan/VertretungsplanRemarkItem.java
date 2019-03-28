package com.rmkrings.data.vertretungsplan;

import com.rmkrings.helper.StringHelper;

public class VertretungsplanRemarkItem extends VertretungsplanListItem {

    private String remarkText;

    public VertretungsplanRemarkItem(String remarkText) {
        this.remarkText = StringHelper.replaceHtmlEntities(remarkText);
    }

    public String getRemarkText() {
        return remarkText;
    }

    @Override
    public int getType() {
        return remarkItem;
    }
}
