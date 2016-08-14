package org.tmreynolds.advertisers.model;

public class FooterItem extends ListItem {

    public int totalImpressionsByDate;

    public int getTotalImpressionsByDate() { return totalImpressionsByDate ; }

    @Override
    public int getType() {
        return TYPE_FOOTER;
    }

}
