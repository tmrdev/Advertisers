package org.tmreynolds.advertisers.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmrdev on 7/26/16.
 */
public class Advertiser {

    @SerializedName("num_impressions")
    public int impressionsTotal;

    //public int getAdvertiserId() { return advertiserId; }

    public int getImpressionsTotal() { return impressionsTotal; }

}
