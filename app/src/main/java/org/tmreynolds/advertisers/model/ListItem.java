package org.tmreynolds.advertisers.model;

/**
 * Created by tmrdev on 8/13/16.
 */
public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_EVENT = 1;
    public static final int TYPE_FOOTER = 2;

    abstract public int getType();
}

