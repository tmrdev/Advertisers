package org.tmreynolds.advertisers;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.tmreynolds.advertisers.adapter.AdvertiserRecyclerAdapter;
import org.tmreynolds.advertisers.model.Advertisers;
import org.tmreynolds.advertisers.model.Details;
import org.tmreynolds.advertisers.rest.AdvertisersInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AdvertiserRecyclerAdapter adapter;
    private ProgressBar progressBar;
    private static int currentApiCall = 0;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public TreeMap<String, List<Details>> dateAdvertiserData = new TreeMap<String, List<Details>>();
    public List<Advertisers> mAdvertisers = new ArrayList<>();
    List<String> advertiserDates = new ArrayList<>();
    public static final String BASE_URL = "http://dan.triplelift.net/";

    public static double msDuration = 0;

    /*
     * Basic TreeMap structure is working and data point tests showing sorting working and ad totals verified
     * - should mItems be processed before calling recycler adapter?
     * - Need to make the layout cleaner with good date header layout (color background and other styling?
     * - Need to total all impressions for the day ( do this before hitting recycler adaptor?
     * - Need an efficient way to measure the time it takes to call all three api calls
     *  - Will dev tools like mockito and others help with this?
     */
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
        // *** NOTE: *** init empty recyclerview and set adapter
        adapter = new AdvertiserRecyclerAdapter(dateAdvertiserData, R.layout.list_item_ad_ids, getApplicationContext());
        mRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        Log.i("call", "before api method call");

        getAdvertiserIds(new long[]{145, 298, 898});
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    private void refreshItems() {
        // Load items
        currentApiCall = 0;
        // clear data sort lists
        dateAdvertiserData.clear();
        advertiserDates.clear();
        mAdvertisers.clear();
        // clear recycler adapter
        adapter.clearItems();
        // Load complete
        onItemsLoadComplete();
    }

    private int getRandomNumber() {
        int randomNumber = (int)(Math.random()*900)+100;
        return randomNumber;
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        //mRecyclerView.getAdapter().notifyDataSetChanged();
        getAdvertiserIds(new long[]{getRandomNumber(), getRandomNumber(), getRandomNumber()});
        adapter.notifyDataSetChanged();
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getAdvertiserIds(long[] advertiserIds){
        // Trailing slash is needed
        Log.i("call", "top of get ad api call");
        int totalApiCalls = advertiserIds.length;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!


        // custom inner class does not work
        //OkHttpClient okHttpClient = new OkHttpClient();
        //okHttpClient.networkInterceptors().add(new LoggingInterceptor());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        long t1 = System.nanoTime();
                        // logger.info(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
                        Log.i("test", "test--->" + String.format(Locale.US, "Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

                        okhttp3.Response response = chain.proceed(request);

                        long t2 = System.nanoTime();
                        // logger.info(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                        double apiCallMsDuration = (t2 - t1) / 1e6d;

                        Log.i("test", "test-->" + String.format(Locale.US, "Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                        long start = response.sentRequestAtMillis();
                        long end = response.receivedResponseAtMillis();
                        long msDuration = end - start;
                        //Log.i("test", "off->response-> duration ->" + apiCallMsDuration);
                        //MainActivity.msDuration = apiCallMsDuration;
                        Log.i("test", "off->response-> duration ->" + msDuration);
                        MainActivity.msDuration = msDuration;
                        return response;
                        /*
                        if (BuildConfig.DEBUG) {
                            Log.i(getClass().getName(), "request->method->" + request.method() + " " + request.url());
                            Log.i(getClass().getName(), "cookie?->" + request.header("Cookie"));
                            RequestBody rb = request.body();
                            Buffer buffer = new Buffer();
                            if (rb != null) {
                                rb.writeTo(buffer);
                                Log.i(getClass().getName(), "Payload- " + buffer.readUtf8());
                                //LogUtils.LOGE(getClass().getName(), "Payload- " + buffer.readUtf8());
                            }

                        }
                        return chain.proceed(request);
                        */
                    }
                })
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        //RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        // builder needs rxAdapter set
        //.addCallAdapterFactory(rxAdapter)

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                // retrofit 2 loglevel replacement
                // .client(httpClient.build)
                .client(okHttpClient)
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
        // testing api execution with stopwatch
        //long startAPIWatch = System.currentTimeMillis();
        // get request time from in ms from this:
        // https://futurestud.io/blog/retrofit-2-log-requests-and-responses
        Call<List<Advertisers>> call = apiService.getAdvertiser(advertiserId);



        call.enqueue(new Callback<List<Advertisers>>() {

            @Override
            public void onResponse(Call<List<Advertisers>> call, Response<List<Advertisers>> response) {
                currentApiCall++;
                //long totalTimeApiCalls = System.currentTimeMillis() - startAPIWatch;
                //String bodyString = new String(response.body().);

                //Log.i("bodyr", " body String -> " + response. );

                //Log.i("callapitime", "total api call time -> " + totalTimeApiCalls + " : " + response.headers());

                Log.i("onr", "current api call -> " + currentApiCall + " : total -> " + totalApiCalls + " : duration -> " + MainActivity.msDuration);
                Log.i("call", "api call status -> " + response.code() + " : " + response.body());
                Log.i("call", "data -> " + response.body().size());

                int totalResults = response.body().size();
                List<Advertisers> advertiser = response.body();

                for (int i = 0; i < totalResults; i++) {
                    if(MainActivity.msDuration > 200) {
                        // is api call takes longer than 200ms then flag all entries as timed out
                        advertiser.get(i).setIsTimedOut(true);
                    }
                    Log.i("call", "data -> " + response.body().get(i).getImpressionsTotal());
                    Log.i("call", "date data -> " + advertiser.get(i).getGroupDate());
                }
                mAdvertisers.addAll(advertiser);
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
                    adapter = new AdvertiserRecyclerAdapter(dateAdvertiserData, R.layout.list_item_ad_ids, getApplicationContext());
                    mRecyclerView.setAdapter(adapter);

                }

                /* data dump testing below


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

                * end data dump testing
                */

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
                // flag as timed out past 200ms
                if(mAdvertisers.get(i).getIsTimedOut()) {itemDetails.isTimedOut = true;}
                groupDetails.add(itemDetails);
            }

        }
        return groupDetails;
    }


    private class LoggingInterceptor implements okhttp3.Interceptor {
        public Logger logger;

        @Override public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            logger.info(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            okhttp3.Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            logger.info(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }
}
