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
public class AdvertiserRecyclerAdatper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //private List<Advertisers> advertisers;
    TreeMap<String, List<Details>> advertisers;
    private int rowLayout;
    private Context context;

    List<ListItem> mItems;

    // http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview

    /*
    public static class AdvertiserViewHolder extends RecyclerView.ViewHolder{

        TextView totalImpressions;
        TextView advertiserId;

        public AdvertiserViewHolder(View itemView) {
            super(itemView);
            totalImpressions = (TextView) itemView.findViewById(R.id.total_impressions);
            advertiserId = (TextView) itemView.findViewById(R.id.advertiser_id);
        }
    }
    */

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

    public AdvertiserRecyclerAdatper(TreeMap<String, List<Details>> advertisers, int rowLayout, Context context){
        // NOTE: Entire treemap is placed in advertisers for now, rename or remove if not needed
        this.advertisers = advertisers;
        // using mItems for now to populate grouped recyclerview
        mItems = new ArrayList<>();
        for (String dateString : advertisers.keySet()) {
            HeaderItem header = new HeaderItem();
            header.dateString = dateString;
            mItems.add(header);

            for (Details details : advertisers.get(dateString)) {
                EventItem item = new EventItem();
                // ("details", "details-> " + item.details.advertiserId + " : " + item.details.getImpressions());
                item.advertiserId = details.getAdvertiserId();
                item.totalImpressions = details.getImpressions();
                mItems.add(item);
            }

            FooterItem footer = new FooterItem();
            // add up total for day
            footer.totalImpressionsByDate = 55;
            mItems.add(footer);

            // remove dev code below
            //List<Details> details = advertisers.get(dateString);
            //EventItem adEvents = new EventItem();
            // copying details events from api data merge
            //adEvents.adDetails = details;
            //mItems.add(details);



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
            holder.advertiserId.setText(advertiserIdValue);
            holder.totalImpressions.setText(totalImpressionsValue);
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

    /*
    @Override
    public AdvertiserRecyclerAdatper.AdvertiserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.rowLayout, parent, false);

        Log.i("oCVH", "viewType -> " + viewType);

        return new AdvertiserViewHolder(view);
    }
    */

    /*
    @Override
    public void onBindViewHolder(AdvertiserViewHolder holder, final int position) {
        Log.i("poistiond", "position dump from bind -> " + position);
        // dev dump code takes advertisers tree map to dump values
        // looking to use mItems List with abstract class to display group by date
        Object key = advertisers.keySet().toArray()[position];
        Log.i("bind", "bind -> key -> " + key.toString());
        // puts all data from dateString key into list for that day
        // this will be refactored with group models
        List<Details> details = advertisers.get(key);
        // dumping the Details list values will not have a position to deal with
        // if the above key position can be obtained directly then will need to dump out all detail values
        //Log.i("bind", "details -> " + details.get(0).getAdvertiserId());
        String advertiserIdValue = String.valueOf(key);
        holder.advertiserId.setText(advertiserIdValue);

        Log.i("call", "total impressions -> " + advertisers.get(position).getImpressionsTotal());
        String totalImpressionsValue = String.valueOf(advertisers.get(position).getImpressionsTotal());
        String advertiserIdValue = String.valueOf(advertisers.get(position).getAdvertiserId());
        holder.totalImpressions.setText(totalImpressionsValue);
        holder.advertiserId.setText(advertiserIdValue);

//        Log.i("call", "total impressions -> " + advertisers.get(position).get(position).getAdvertiserId());

        //advertisers.get(position).getImpressionsTotal());

        String totalImpressionsValue = String.valueOf(advertisers.get(position).getImpressionsTotal());
        String advertiserIdValue = String.valueOf(advertisers.get(position).getAdvertiserId());
        holder.totalImpressions.setText(totalImpressionsValue);
        holder.advertiserId.setText(advertiserIdValue);

    }
    */

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 1 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        /*
        int viewTypePosition = position % 2;
        Log.i("givt", " view type position -> " + viewTypePosition);
        return viewTypePosition;
        */

        // NOTE Header and Ad Events override ListItem
        return mItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return advertisers.size();
    }
}
