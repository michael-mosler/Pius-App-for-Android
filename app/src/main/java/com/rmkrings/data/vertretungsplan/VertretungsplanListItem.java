package com.rmkrings.data.vertretungsplan;

public abstract class VertretungsplanListItem {
    public static final int courseHeader = 0;
    public static final int detailItem = 1;
    public static final int remarkItem = 2;
    public static final int evaItem = 3;

    abstract public int getType();
}

