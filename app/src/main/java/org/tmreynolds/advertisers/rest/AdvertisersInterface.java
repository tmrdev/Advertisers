package org.tmreynolds.advertisers.rest;

import org.tmreynolds.advertisers.model.Advertiser;

import java.util.List;

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
    Call<List<Advertiser>> getAdvertiser(@Query("advertiser_id") int advertiserId);

}
