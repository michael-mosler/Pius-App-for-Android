package com.rmkrings.data.eva;

import com.rmkrings.data.MessageItem;

abstract public class EvaListItem extends MessageItem {
    public static final int evaDateItem  = 0;
    public static final int evaCourseItem = 1;

    abstract public int getType();
}

