package org.tmreynolds.advertisers.model;

import com.google.gson.annotations.SerializedName;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tmrdev on 7/26/16.
 */
public class Advertisers {

    @SerializedName("advertiser_id")
    public int advertiserId;

    @SerializedName("num_impressions")
    public int impressionsTotal;

    @SerializedName("ymd")
    public String groupDate;

    public boolean isTimedOut = false;

    public void setIsTimedOut(boolean isTimedOut) {
        this.isTimedOut = isTimedOut;
    }

    public boolean getIsTimedOut() { return isTimedOut; }
    public int getAdvertiserId() { return advertiserId; }
    public int getImpressionsTotal() { return impressionsTotal; }
    public String getGroupDate() {
        Log.i("date", "date -> " + groupDate);
        // SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat displayDate = new SimpleDateFormat("MM/dd/yyyy");
        Date createDate = null;
        try {
            createDate = formatter.parse(groupDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate = displayDate.format(createDate);
        Log.i("formatted", "formatted date -> " + formattedDate);
        return formattedDate;
        //return formatter.format(createDate);
        //return createDate;
    }

}
