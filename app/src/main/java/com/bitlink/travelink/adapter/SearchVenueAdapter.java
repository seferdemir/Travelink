package com.bitlink.travelink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bitlink.travelink.R;
import com.bitlink.travelink.model.foursquare.Venue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchVenueAdapter extends BaseAdapter {

    public class ViewHolder {
        TextView txtTitle, txtSubTitle;
    }

    public List<Venue> venueList;

    public Context context;
    ArrayList<Venue> arrayList;

    private SearchVenueAdapter(List<Venue> apps, Context context) {
        this.venueList = apps;
        this.context = context;
        arrayList = new ArrayList<Venue>();
        arrayList.addAll(venueList);

    }

    @Override
    public int getCount() {
        return venueList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.search_list_item, null);
            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView) rowView.findViewById(R.id.title);
            viewHolder.txtSubTitle = (TextView) rowView.findViewById(R.id.subtitle);
            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtTitle.setText(venueList.get(position).getName() + "");
        viewHolder.txtSubTitle.setText(venueList.get(position).getLocation().getCity() + "");
        return rowView;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        venueList.clear();
        if (charText.length() == 0) {
            venueList.addAll(arrayList);

        } else {
            for (Venue venue : arrayList) {
                if (charText.length() != 0 && venue.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    venueList.add(venue);
                } else if (charText.length() != 0 && venue.getLocation().getCity().toLowerCase(Locale.getDefault()).contains(charText)) {
                    venueList.add(venue);
                }
            }
        }
        notifyDataSetChanged();
    }
}