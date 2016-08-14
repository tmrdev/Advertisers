package org.tmreynolds.advertisers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmrdev on 8/13/16.
 */
public class EventItem extends ListItem {

    public int advertiserId;
    public int totalImpressions;

    public int getAdvertiserId()  { return advertiserId; }
    public int getTotalImpressions() { return totalImpressions; }

    @Override
    public int getType() {
        return TYPE_EVENT;
    }
}
