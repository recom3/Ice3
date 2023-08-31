package com.recom3.snow3.activity.trip.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recom3.snow3.R;
import com.recom3.snow3.model.MetricFormat;
import com.recom3.snow3.utility.NumberFormatter;

/**
 * Created by Chus on 29/08/2023.
 */

public class TripInternalDetailsFragment extends Fragment {
    private static final String LEFT_VALUE = "left_value";
    private static final String RIGHT_VALUE = "right_value";
    private static final String TEXT_COLOR_ID = "text_color";
    private static final String TOP_LEFT = "top_left";
    private static final String TOP_RIGHT = "top_right";

    public static TripInternalDetailsFragment newInstance(int textColorId, int topLeftStringIdDescription, int topRightStringIdDescription, MetricFormat leftValue, MetricFormat rightValue) {
        TripInternalDetailsFragment internal = new TripInternalDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(TEXT_COLOR_ID, textColorId);
        args.putInt(TOP_LEFT, topLeftStringIdDescription);
        args.putInt(TOP_RIGHT, topRightStringIdDescription);
        args.putParcelable(LEFT_VALUE, leftValue);
        args.putParcelable(RIGHT_VALUE, rightValue);
        internal.setArguments(args);
        return internal;
    }

    public int getTopLeftStringIdDescription() {
        return getArguments().getInt(TOP_LEFT);
    }

    public int getTopRightStringIdDescription() {
        return getArguments().getInt(TOP_RIGHT);
    }

    public MetricFormat getLeftValue() {
        return (MetricFormat) getArguments().getParcelable(LEFT_VALUE);
    }

    public MetricFormat getRightValue() {
        return (MetricFormat) getArguments().getParcelable(RIGHT_VALUE);
    }

    public int getTextColorId() {
        return getArguments().getInt(TEXT_COLOR_ID);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflated = inflater.inflate(R.layout.trip_internal_details_fragment, container, false);
        setText(viewInflated);
        setTextColor(getTextColorId(), viewInflated);
        return viewInflated;
    }

    private void setText(View viewInflated) {
        setTextInTextView(R.id.trip_details_internal_top_left_description, getTopLeftStringIdDescription(), viewInflated);
        setTextInTextView(R.id.trip_details_internal_top_right_description, getTopRightStringIdDescription(), viewInflated);
        MetricFormat leftValue = getLeftValue();
        setTextInTextView(R.id.trip_details_internal_fragment_text_view_bottom_left_value, NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(leftValue.getValue()), viewInflated);
        setTextInTextView(R.id.trip_details_internal_fragment_text_view_bottom_left_metric, leftValue.getSuffix().suffix(), viewInflated);
        MetricFormat rightValue = getRightValue();
        setTextInTextView(R.id.trip_details_internal_fragment_text_view_bottom_right_value, NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(rightValue.getValue()), viewInflated);
        setTextInTextView(R.id.trip_details_internal_fragment_text_view_bottom_right_metric, rightValue.getSuffix().suffix(), viewInflated);
    }

    private void setTextColor(int textColorId, View viewInflated) {
        setColor(textColorId, R.id.trip_details_internal_fragment_text_view_bottom_left_value, viewInflated);
        setColor(textColorId, R.id.trip_details_internal_fragment_text_view_bottom_left_metric, viewInflated);
        setColor(textColorId, R.id.trip_details_internal_fragment_text_view_bottom_right_value, viewInflated);
        setColor(textColorId, R.id.trip_details_internal_fragment_text_view_bottom_right_metric, viewInflated);
    }

    private void setTextInTextView(int viewId, int stringId, View viewInflated) {
        ((TextView) viewInflated.findViewById(viewId)).setText(stringId);
    }

    private void setTextInTextView(int viewId, CharSequence string, View viewInflated) {
        ((TextView) viewInflated.findViewById(viewId)).setText(string);
    }

    private void setColor(int colorId, int viewId, View viewInflated) {
        ((TextView) viewInflated.findViewById(viewId)).setTextColor(getResources().getColor(colorId));
    }
}