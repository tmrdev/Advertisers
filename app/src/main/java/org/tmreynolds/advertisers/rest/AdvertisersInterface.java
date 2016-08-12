package org.tmreynolds.advertisers.rest;

import org.tmreynolds.advertisers.model.Advertisers;

import java.util.List;
import java.util.Observable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tmrdev on 7/26/16.
 */
public interface AdvertisersInterface {

    @GET("code_test.php?")
    Call<List<Advertisers>> getAdvertiser(@Query("advertiser_id") long advertiserId);


    //@GET("code_test.php?")
    //Observable<List<Advertisers>> getAdvertiser(@Query("advertiser_id") long advertiserId);
}
