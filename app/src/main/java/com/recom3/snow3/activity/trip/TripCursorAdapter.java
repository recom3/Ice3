package com.recom3.snow3.activity.trip;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.recom3.mobilesdk.buddytracking.Buddy;
import com.recom3.snow3.R;
import com.recom3.snow3.mobilesdk.tripviewer.Trip;
import com.recom3.snow3.model.MetricFormat;
import com.recom3.snow3.service.AirwaveService;
import com.recom3.snow3.service.TripAirwaveService;
import com.recom3.snow3.util.validation.MetricFormatter;
import com.recom3.snow3.utility.DateFormatter;
import com.recom3.snow3.utility.NumberFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Recom3 on 27/05/2023.
 */

public class TripCursorAdapter extends BaseAdapter {

    private final ArrayList mData;

    private Activity mActivity;
    private TripAirwaveService mAirwaveService;
    private HashMap<String, ArrayList<Trip>> mEntries;
    private Object[] mKeys;


    public static class TripItemViewHolder {
        public TextView dayOfMonth;
        public TextView maxSpeed;
        public TextView maxSpeedSuffix;
        public TextView place;
        public TextView runs;
        public TextView totalVertical;
        public TextView totalVerticalSuffix;
    }

    /*
    public TripItemArrayAdapter(Activity activity, int textViewResourceId,
                                HashMap<String, ArrayList<Trip>> entries,
                                AirwaveService airwaveService) {
        this.mAirwaveService = airwaveService;
        this.mKeys = entries.keySet().toArray();
        this.mEntries = entries;
        this.mActivity = activity;
    }
    */

    public TripCursorAdapter(ArrayList<Trip> map, TripAirwaveService airwaveService) {

        mData = map;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Trip getItem(int position) {
        return (Trip)mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        TripItemViewHolder tripItemHolder;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.trips_list_item_layout, parent, false);
            tripItemHolder = new TripItemViewHolder();
            tripItemHolder.dayOfMonth = (TextView) result.findViewById(R.id.trips_list_item_layout_day_of_month);
            tripItemHolder.place = (TextView) result.findViewById(R.id.trips_list_item_layout_place);
            tripItemHolder.runs = (TextView) result.findViewById(R.id.trips_list_item_layout_runs);
            tripItemHolder.totalVertical = (TextView) result.findViewById(R.id.trips_list_item_layout_total_vertical);
            tripItemHolder.totalVerticalSuffix = (TextView) result.findViewById(R.id.trips_list_item_layout_total_vertical_suffix);
            tripItemHolder.maxSpeed = (TextView) result.findViewById(R.id.trips_list_item_layout_max_speed);
            tripItemHolder.maxSpeedSuffix = (TextView) result.findViewById(R.id.trips_list_item_layout_max_speed_suffix);
            result.setTag(tripItemHolder);

        } else {
            result = convertView;
            tripItemHolder = (TripItemViewHolder) result.getTag();
        }

        Trip item = getItem(position);

        //((TextView) result.findViewById(R.id.notifications_row_time)).setText(item.trip_created);
        //((TextView) result.findViewById(R.id.notifications_row_info)).setText(item.trip_country);

        //Trip trip = this.mEntries.get(this.mKeys[section]).get(position);
        Trip trip = item;
        if (trip != null) {
            int dayOfMonth = dayOfMonth(trip);
            if(tripItemHolder.dayOfMonth!=null)
                tripItemHolder.dayOfMonth.setText(DateFormatter.getDayWithSuffix(dayOfMonth));
            tripItemHolder.place.setText(String.valueOf(trip.trip_city) + ", " + trip.trip_country);
            tripItemHolder.runs.setText(trip.trip_num_segments);
            setTotalVertical(tripItemHolder, trip);
            setMaxSpeed(trip, tripItemHolder);
        }

        return result;
    }

    private int dayOfMonth(Trip trip) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(Long.parseLong(trip.trip_day_first_timestamp));
        return instance.get(Calendar.DAY_OF_MONTH);
    }

    private void setTotalVertical(TripItemViewHolder tripItemHolder, Trip trip) {
        //!!!
        //MetricFormat totalVertical = this.mAirwaveService.getCurrentMetricFromMeterWithValue(trip.trip_total_vertical);
        double dData = 0.0d;
        try{
            dData = Double.parseDouble(trip.trip_total_vertical);
        }
        catch (Exception ex)
        {

        }
        MetricFormat totalVertical = new MetricFormat(MetricFormat.MetricSuffix.METER, dData);
        tripItemHolder.totalVertical.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(Math.abs(totalVertical.getValue())));
        tripItemHolder.totalVerticalSuffix.setText(totalVertical.getSuffix().suffix());
    }

    private void setMaxSpeed(Trip trip, TripItemViewHolder tripItemHolder) {
        //MetricFormat maxSpeed = this.mAirwaveService.getCurrentMetricFromMeterWithValue(trip.trip_max_speed);
        double dData = 0.0d;
        try{
            dData = Double.parseDouble(trip.trip_max_speed);
        }
        catch (Exception ex)
        {

        }
        MetricFormat maxSpeed = new MetricFormat(MetricFormat.MetricSuffix.KILOMETER, dData);
        /*
        if (this.mAirwaveService.isMilesMetric()) {
            maxSpeed = MetricFormatter.formatMilesPerHour(Double.parseDouble(trip.trip_max_speed));
        }
        */
        tripItemHolder.maxSpeed.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(maxSpeed.getValue()));
        //tripItemHolder.maxSpeedSuffix.setText(maxSpeed.getSuffix().suffix());
    }

}
