package org.tmreynolds.advertisers.model;

public class HeaderItem extends ListItem {

    //private Date date;
    public String dateString;

    public String getDateString() { return dateString; }

    // here getters and setters
    // for title and so on, built
    // using date

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

}
