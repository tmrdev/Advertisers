package org.tmreynolds.advertisers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tmreynolds.advertisers.R;
import org.tmreynolds.advertisers.model.Advertiser;

import java.util.List;

/**
 * Created by tmrdev on 7/26/16.
 */
public class AdvertiserRecyclerAdatper extends RecyclerView.Adapter<AdvertiserRecyclerAdatper.AdvertiserViewHolder> {


    private List<Advertiser> advertiser;
    private int rowLayout;
    private Context context;

    public static class AdvertiserViewHolder extends RecyclerView.ViewHolder{

        TextView totalImpressions;

        public AdvertiserViewHolder(View itemView) {
            super(itemView);
            totalImpressions = (TextView) itemView.findViewById(R.id.total_impressions);
        }
    }

    public AdvertiserRecyclerAdatper(List<Advertiser> advertiser, int rowLayout, Context context){
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
    public void onBindViewHolder(AdvertiserViewHolder holder, final int position) {
        Log.i("call", "total impressions -> " + advertiser.get(position).getImpressionsTotal());
        String totalImpressions = String.valueOf(advertiser.get(position).getImpressionsTotal());
        holder.totalImpressions.setText(totalImpressions);
    }


    @Override
    public int getItemCount() {
        return advertiser.size();
    }
}