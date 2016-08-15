package org.tmreynolds.advertisers.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tmreynolds.advertisers.MainActivity;
import org.tmreynolds.advertisers.R;
import org.tmreynolds.advertisers.model.Details;
import org.tmreynolds.advertisers.model.FooterItem;
import org.tmreynolds.advertisers.model.HeaderItem;
import org.tmreynolds.advertisers.model.EventItem;
import org.tmreynolds.advertisers.model.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by tmrdev on 7/26/16.
 */
public class AdvertiserRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //private List<Advertisers> advertisers;
    TreeMap<String, List<Details>> advertisers = new TreeMap<String, List<Details>>();
    private int rowLayout;
    private Context context;

    public List<ListItem> mItems = new ArrayList<>();

    // Date Grouping View Holder
    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView dateGroup;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            dateGroup = (TextView) itemView.findViewById(R.id.date_grouping);
        }
    }

    private class AdvertisersViewHolder extends RecyclerView.ViewHolder {

        TextView totalImpressions;
        TextView advertiserId;

        public AdvertisersViewHolder(View itemView) {
            super(itemView);
            totalImpressions = (TextView) itemView.findViewById(R.id.total_impressions);
            advertiserId = (TextView) itemView.findViewById(R.id.advertiser_id);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        TextView totalDayImpressions;

        public FooterViewHolder(View itemView) {
            super(itemView);
            totalDayImpressions = (TextView) itemView.findViewById(R.id.total_by_date_impressions);
        }
    }

    public AdvertiserRecyclerAdapter(TreeMap<String, List<Details>> advertisers, int rowLayout, Context context){
        // NOTE: Entire treemap is placed in advertisers for now, rename or remove if not needed
        //clearItems();
        this.advertisers = advertisers;
        // using mItems for now to populate grouped recyclerview
        mItems = new ArrayList<>();
        for (String dateString : this.advertisers.keySet()) {
            int dayImpressionTotal = 0;
            HeaderItem header = new HeaderItem();
            header.dateString = dateString;
            mItems.add(header);

            for (Details details : this.advertisers.get(dateString)) {
                EventItem item = new EventItem();
                // ("details", "details-> " + item.details.advertiserId + " : " + item.details.getImpressions());
                item.advertiserId = details.getAdvertiserId();
                item.totalImpressions = details.getImpressions();
                item.isTimedOut = details.getIsTimedOut();
                // only total values that have not timed out
                if(!item.isTimedOut) {
                    dayImpressionTotal += item.getTotalImpressions();
                }
                mItems.add(item);
            }

            FooterItem footer = new FooterItem();
            // add up total for day
            footer.totalImpressionsByDate = dayImpressionTotal;
            mItems.add(footer);
        }

        this.rowLayout = rowLayout;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_top_by_date, parent, false);
            return new HeaderViewHolder(itemView);
        } else if(viewType == ListItem.TYPE_EVENT) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_advertisers, parent, false);
            return new AdvertisersViewHolder(itemView);
        } else if(viewType == ListItem.TYPE_FOOTER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bottom_total, parent, false);
            return new FooterViewHolder(itemView);
        } else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        Log.i("onbind", "onbind get type -> " + type);

        if (type == ListItem.TYPE_HEADER) {
            HeaderItem header = (HeaderItem) mItems.get(position);
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            holder.dateGroup.setText(header.getDateString());
            // your logic here
        } else if(type == ListItem.TYPE_EVENT) {
            Log.i("onbind", "hit Ad Event!");
            EventItem event = (EventItem) mItems.get(position);
            AdvertisersViewHolder holder = (AdvertisersViewHolder) viewHolder;
            String advertiserIdValue = String.valueOf(event.getAdvertiserId());
            String totalImpressionsValue = String.valueOf(event.getTotalImpressions());
            if(event.getIsTimedOut()) {
                holder.advertiserId.setText(advertiserIdValue);
                holder.totalImpressions.setText("Timed Out");
                holder.totalImpressions.setTextColor(Color.RED);
            } else {
                holder.advertiserId.setText(advertiserIdValue);
                holder.totalImpressions.setText(totalImpressionsValue);
            }

            // your logic here
        } else if(type == ListItem.TYPE_FOOTER) {
            FooterItem footer = (FooterItem) mItems.get(position);
            FooterViewHolder holder = (FooterViewHolder) viewHolder;
            // convert to string
            String totalImpByDateValue = String.valueOf(footer.getTotalImpressionsByDate());
            holder.totalDayImpressions.setText(totalImpByDateValue);
        } else {
            // null default trap
        }
    }

    public void clearItems() {
        if( mItems != null && mItems.size() > 0) {
            this.advertisers.clear();
            mItems.clear();
            //notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
