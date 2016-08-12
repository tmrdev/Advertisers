package org.tmreynolds.advertisers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tmreynolds.advertisers.MainActivity;
import org.tmreynolds.advertisers.R;
import org.tmreynolds.advertisers.model.Advertisers;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by tmrdev on 7/26/16.
 */
public class AdvertiserRecyclerAdatper extends RecyclerView.Adapter<AdvertiserRecyclerAdatper.AdvertiserViewHolder> {


    //private List<Advertisers> advertiser;
    TreeMap<String, List<MainActivity.Details>> advertiser;
    private int rowLayout;
    private Context context;
    // http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview

    public static class AdvertiserViewHolder extends RecyclerView.ViewHolder{

        TextView totalImpressions;
        TextView advertiserId;

        public AdvertiserViewHolder(View itemView) {
            super(itemView);
            totalImpressions = (TextView) itemView.findViewById(R.id.total_impressions);
            advertiserId = (TextView) itemView.findViewById(R.id.advertiser_id);
        }
    }

    public AdvertiserRecyclerAdatper(TreeMap<String, List<MainActivity.Details>> advertiser, int rowLayout, Context context){
        this.advertiser = advertiser;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public AdvertiserRecyclerAdatper.AdvertiserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.rowLayout, parent, false);
        return new AdvertiserViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 1 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        int viewTypePosition = position % 2;
        Log.i("givt", " view type position -> " + viewTypePosition);
        return viewTypePosition;
    }

    @Override
    public void onBindViewHolder(AdvertiserViewHolder holder, final int position) {
        /*
        Log.i("call", "total impressions -> " + advertiser.get(position).getImpressionsTotal());
        String totalImpressionsValue = String.valueOf(advertiser.get(position).getImpressionsTotal());
        String advertiserIdValue = String.valueOf(advertiser.get(position).getAdvertiserId());
        holder.totalImpressions.setText(totalImpressionsValue);
        holder.advertiserId.setText(advertiserIdValue);
        */
    }


    @Override
    public int getItemCount() {
        return advertiser.size();
    }
}
