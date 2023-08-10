package com.recom3.snow3.activity.buddy;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.recom3.mobilesdk.buddytracking.Buddy;
import com.recom3.snow3.MainActivityTest;
import com.recom3.snow3.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Recom3 on 27/05/2023.
 */

public class BuddyCursorAdapter extends BaseAdapter {

    private final ArrayList mData;

    public BuddyCursorAdapter(HashMap<String, Buddy> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, Buddy> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_list_item, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, Buddy> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.notifications_row_time)).setText(item.getKey());
        Buddy buddy = (Buddy)item.getValue();
        ((TextView) result.findViewById(R.id.notifications_row_info)).setText(item.getValue().getName());

        return result;
    }
}
