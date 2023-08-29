package com.recom3.snow3.activity.trip;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recom3.snow3.R;
import com.recom3.snow3.mobilesdk.tripviewer.Trip;
import com.recom3.snow3.model.MetricFormat;
import com.recom3.snow3.service.AirwaveService;
import com.recom3.snow3.util.validation.MetricFormatter;
import com.recom3.snow3.utility.DateFormatter;
import com.recom3.snow3.utility.NumberFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Chus on 28/08/2023.
 */

public class TripItemArrayAdapter{
        //extends SectionedBaseAdapter {

    private Activity mActivity;
    private AirwaveService mAirwaveService;
    private HashMap<String, ArrayList<Trip>> mEntries;
    private Object[] mKeys;

    /* loaded from: classes.dex */
    public static class TripItemViewHolder {
        public TextView dayOfMonth;
        public TextView maxSpeed;
        public TextView maxSpeedSuffix;
        public TextView place;
        public TextView runs;
        public TextView totalVertical;
        public TextView totalVerticalSuffix;
    }

    public TripItemArrayAdapter(Activity activity, int textViewResourceId, HashMap<String, ArrayList<Trip>> entries, AirwaveService airwaveService) {
        this.mAirwaveService = airwaveService;
        this.mKeys = entries.keySet().toArray();
        this.mEntries = entries;
        this.mActivity = activity;
    }

    //@Override // za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter
    public Object getItem(int section, int position) {
        return this.mEntries.get(this.mKeys[section]).get(position);
    }

    //@Override // za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter
    public long getItemId(int section, int position) {
        return 0L;
    }

    //@Override // za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter
    public int getSectionCount() {
        return this.mKeys.length;
    }

    //@Override // za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter
    public int getCountForSection(int section) {
        return this.mEntries.get(this.mKeys[section]).size();
    }

    //@Override // za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        TripItemViewHolder tripItemHolder;
        View view = convertView;
        if (view == null) {
            LayoutInflater viewInflate = LayoutInflater.from(this.mActivity);
            view = viewInflate.inflate(R.layout.trips_list_item_layout, (ViewGroup) null);
            tripItemHolder = new TripItemViewHolder();
            tripItemHolder.dayOfMonth = (TextView) view.findViewById(R.id.trips_list_item_layout_day_of_month);
            tripItemHolder.place = (TextView) view.findViewById(R.id.trips_list_item_layout_place);
            tripItemHolder.runs = (TextView) view.findViewById(R.id.trips_list_item_layout_runs);
            tripItemHolder.totalVertical = (TextView) view.findViewById(R.id.trips_list_item_layout_total_vertical);
            tripItemHolder.totalVerticalSuffix = (TextView) view.findViewById(R.id.trips_list_item_layout_total_vertical_suffix);
            tripItemHolder.maxSpeed = (TextView) view.findViewById(R.id.trips_list_item_layout_max_speed);
            tripItemHolder.maxSpeedSuffix = (TextView) view.findViewById(R.id.trips_list_item_layout_max_speed_suffix);
            view.setTag(tripItemHolder);
        } else {
            tripItemHolder = (TripItemViewHolder) view.getTag();
        }
        Trip trip = this.mEntries.get(this.mKeys[section]).get(position);
        if (trip != null) {
            int dayOfMonth = dayOfMonth(trip);
            tripItemHolder.dayOfMonth.setText(DateFormatter.getDayWithSuffix(dayOfMonth));
            tripItemHolder.place.setText(String.valueOf(trip.trip_city) + ", " + trip.trip_country);
            tripItemHolder.runs.setText(trip.trip_num_segments);
            setTotalVertical(tripItemHolder, trip);
            setMaxSpeed(trip, tripItemHolder);
        }
        return view;
    }

    private void setTotalVertical(TripItemViewHolder tripItemHolder, Trip trip) {
        MetricFormat totalVertical = this.mAirwaveService.getCurrentMetricFromMeterWithValue(trip.trip_total_vertical);
        tripItemHolder.totalVertical.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(Math.abs(totalVertical.getValue())));
        tripItemHolder.totalVerticalSuffix.setText(totalVertical.getSuffix().suffix());
    }

    private void setMaxSpeed(Trip trip, TripItemViewHolder tripItemHolder) {
        MetricFormat maxSpeed = this.mAirwaveService.getCurrentMetricFromMeterWithValue(trip.trip_max_speed);
        if (this.mAirwaveService.isMilesMetric()) {
            maxSpeed = MetricFormatter.formatMilesPerHour(Double.parseDouble(trip.trip_max_speed));
        }
        tripItemHolder.maxSpeed.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(maxSpeed.getValue()));
        tripItemHolder.maxSpeedSuffix.setText(maxSpeed.getSuffix().suffix());
    }

    private int dayOfMonth(Trip trip) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(Long.parseLong(trip.trip_day_first_timestamp));
        return instance.get(5);
    }

    //@Override // za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter, za.co.immedia.pinnedheaderlistview.PinnedHeaderListView.PinnedSectionedHeaderAdapter
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService("layout_inflater");
            layout = (LinearLayout) inflator.inflate(R.layout.trips_list_header_layout, (ViewGroup) null);
        } else {
            layout = (LinearLayout) convertView;
        }
        ((TextView) layout.findViewById(R.id.trips_list_header_layout_text)).setText((CharSequence) this.mKeys[section]);
        return layout;
    }
}
