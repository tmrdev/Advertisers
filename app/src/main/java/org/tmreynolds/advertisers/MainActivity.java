package org.tmreynolds.advertisers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.tmreynolds.advertisers.adapter.AdvertiserRecyclerAdatper;
import org.tmreynolds.advertisers.model.Advertisers;
import org.tmreynolds.advertisers.rest.AdvertisersInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AdvertiserRecyclerAdatper adapter;
    private ProgressBar progressBar;
    private static int currentApiCall = 0;

    public TreeMap<String, List<Details>> dateAdvertiserData = new TreeMap<String, List<Details>>();
    public List<Advertisers> mAdvertisers = new ArrayList<>();
    List<String> advertiserDates = new ArrayList<>();
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

        getAdvertiserIds(new long[]{145, 298, 898});

    }

    private void getAdvertiserIds(long[] advertiserIds){
        // Trailing slash is needed
        Log.i("call", "top of get ad api call");
        int totalApiCalls = advertiserIds.length;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        //RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //.addCallAdapterFactory(rxAdapter)
                .client(httpClient.build())
                .build();

        AdvertisersInterface apiService = retrofit.create(AdvertisersInterface.class);
        //int advertiserId = 123;
        currentApiCall = 0;
        for(long advertiserId : advertiserIds) {
            Log.i("aIds", " aid-> " + advertiserId);
            advertiserIdApiCall(apiService, advertiserId, totalApiCalls);
        }

        /*
        for (Map.Entry<String, List<Advertiser>> entry : dateAdvertiserData.entrySet()) {
            String key = entry.getKey();
            Log.i("treemap", "key -> " + key);
            //Log.i("treemap", "treemap key -> " + key + " : " + entry.getValue().get(0).getImpressionsTotal());
        }
        */

    }

    private void advertiserIdApiCall(AdvertisersInterface apiService, long advertiserId, int totalApiCalls){
        Call<List<Advertisers>> call = apiService.getAdvertiser(advertiserId);
        call.enqueue(new Callback<List<Advertisers>>() {
            @Override
            public void onResponse(Call<List<Advertisers>> call, Response<List<Advertisers>> response) {
                currentApiCall++;
                Log.i("onr", "current api call -> " + currentApiCall + " : total -> " + totalApiCalls);
                Log.i("call", "api call status -> " + response.code() + " : " + response.body());
                Log.i("call", "data -> " + response.body().size());
                int totalResults = response.body().size();
                List<Advertisers> advertiser = response.body();
                mAdvertisers.addAll(advertiser);
                for (int i = 0; i < totalResults; i++) {
                    Log.i("call", "data -> " + response.body().get(i).getImpressionsTotal());
                    Log.i("call", "date data -> " + advertiser.get(i).getGroupDate());
                }
                // get all dates used
                setAdvertiserDates(advertiser);
                if(currentApiCall == totalApiCalls) {
                    for(String itemDate : advertiserDates) {
                        Log.i("dates", " date->" + itemDate);
                        List<Details> dateDetails = sortByDate(itemDate);
                        dateAdvertiserData.put(itemDate, dateDetails);
                        Log.i("total", "--->" + dateDetails.size() + ":->" + dateDetails.get(0).getAdvertiserId());
                    }
                    progressBar.setVisibility(View.GONE);
                    // mRecyclerView.setAdapter(new AdvertiserRecyclerAdatper(mAdvertisers, R.layout.list_item_ad_ids, getApplicationContext()));
                    mRecyclerView.setAdapter(new AdvertiserRecyclerAdatper(dateAdvertiserData, R.layout.list_item_ad_ids, getApplicationContext()));
                }
                //Advertiser advertiser = response.body();
                //Log.i("call", "data -> " + advertiser.getDataObjects().get(0).getImpressionsTotal());

                for(String treeKey : dateAdvertiserData.keySet()) {
                    Log.i("treekey", "tree key -> " + treeKey);

                }

                Set keys = dateAdvertiserData.keySet();
                for (Iterator i = keys.iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    Log.i("dumpkeys", " dkeys **-> " + key);
                    List<Details> values = (List <Details>) dateAdvertiserData.get(key);
                    for (int x = 0; x < values.size(); x++) {
                        Log.i("dump", " values **-> " + values.get(x).advertiserId);

                    }
                }


            }

            @Override
            public void onFailure(Call<List<Advertisers>> call, Throwable t) {
                Log.i("call", "onFailure -> " + t.getMessage() + " : " + t.getCause());
            }
        });
    }

    private void setAdvertiserDates(List<Advertisers> advertisers){
        for(Advertisers adEntry : advertisers) {
            if(!Arrays.asList(advertiserDates).contains(adEntry.getGroupDate())) {
               advertiserDates.add(adEntry.getGroupDate());
            }
        }
    }

    public List<Details> sortByDate(String itemDate) {
        List<Details> groupDetails = null;
        groupDetails = new ArrayList<Details>();
        for (int i = 0; i < mAdvertisers.size(); i++) {

            if (mAdvertisers.get(i).getGroupDate().equals(itemDate)) {

                Log.i("filter", "date match -> " + itemDate + " : " + mAdvertisers.get(i).getAdvertiserId() + ": imp->" + mAdvertisers.get(i).getImpressionsTotal());
                Details itemDetails = new Details();
                itemDetails.impressions = mAdvertisers.get(i).getImpressionsTotal();
                itemDetails.advertiserId = mAdvertisers.get(i).getAdvertiserId();
                groupDetails.add(itemDetails);
                // Log.i("dd", " total -->" + groupDetails.size());
                // dateAdvertiserData.put(itemDate, groupDetails);
            }

        }
        return groupDetails;
    }

    public class Details{
        int impressions;
        int advertiserId;
        int numberOfClicks;

        public int getImpressions() { return impressions; }
        public int getAdvertiserId() { return advertiserId; }
        public int getNumberOfClicks() { return numberOfClicks; }

    }
}
