package com.rmkrings.data;

import java.io.Serializable;

public abstract class BaseListItem implements Serializable {
    public static final int message = 100;

    abstract public int getType();
}
