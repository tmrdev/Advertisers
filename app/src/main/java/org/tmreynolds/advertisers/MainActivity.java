package org.tmreynolds.advertisers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.tmreynolds.advertisers.adapter.AdvertiserRecyclerAdatper;
import org.tmreynolds.advertisers.model.Advertiser;
import org.tmreynolds.advertisers.rest.AdvertisersInterface;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AdvertiserRecyclerAdatper adapter;
    private ProgressBar progressBar;

    public static final String BASE_URL = "http://dan.triplelift.net/";

    /*
     * Advertiser ids api call
     * http://dan.triplelift.net/code_test.php?advertiser_id=123
     * method ex: RetrievedData data = yourMethod(new long[]{123, 456, 789});
     * Use integer long array to iterate through each ad id
     *
     * Will need to also measure length of api call and flag if it takes longer then 200ms
     * Might need to use mockito, etc... to measure efficiently
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_advertiser_list);
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.ad_ids_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        Log.i("call", "before api method call");
        getAdvertiserIds();
    }

    private void getAdvertiserIds(){
        // Trailing slash is needed
        Log.i("call", "top of get ad api call");
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        AdvertisersInterface apiService = retrofit.create(AdvertisersInterface.class);
        int advertiserId = 123;
        Call<List<Advertiser>> call = apiService.getAdvertiser(advertiserId);
        call.enqueue(new Callback<List<Advertiser>>() {
            @Override
            public void onResponse(Call<List<Advertiser>> call, Response<List<Advertiser>> response) {
                Log.i("call", "api call status -> " + response.code() + " : " + response.body());
                //List<Advertiser> advertiser = (List<Advertiser>) response.body();
                /* test

                for(Advertiser item : advertiser ) {
                    Log.i("call", "data -> " + item.advertiserId);
                    Log.i("call", "data -> " + item.impressionsTotal);
                }
                */
                Log.i("call", "data -> " + response.body().size());
                int totalResults = response.body().size();
                List<Advertiser> advertiser = response.body();
                for (int i = 0; i < totalResults; i++) {
                    Log.i("call", "data -> " + response.body().get(i).getImpressionsTotal());
                }
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setAdapter(new AdvertiserRecyclerAdatper(advertiser, R.layout.list_item_ad_ids, getApplicationContext()));
                //Advertiser advertiser = response.body();
                //Log.i("call", "data -> " + advertiser.getDataObjects().get(0).getImpressionsTotal());
            }

            @Override
            public void onFailure(Call<List<Advertiser>> call, Throwable t) {
                Log.i("call", "onFailure -> " + t.getMessage() + " : " + t.getCause());
            }
        });
    }
}
