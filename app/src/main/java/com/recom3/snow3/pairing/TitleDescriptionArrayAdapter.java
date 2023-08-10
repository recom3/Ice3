package com.recom3.snow3.pairing;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.recom3.snow3.R;

import java.util.ArrayList;

/**
 * Created by Recom3 on 17/07/2022.
 */

public class TitleDescriptionArrayAdapter<T extends TitleDescriptionModel> extends ArrayAdapter<T> {
    private Activity mActivity;

    private ArrayList<T> mEntries;

    public TitleDescriptionArrayAdapter(Activity paramActivity, int paramInt, ArrayList<T> paramArrayList) {
        super((Context)paramActivity, paramInt, paramArrayList);
        this.mEntries = paramArrayList;
        this.mActivity = paramActivity;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        TitleDescriptionViewHolder titleDescriptionViewHolder;
        if (paramView == null) {
            paramView = ((LayoutInflater)this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.title_description_list_view, null);
            titleDescriptionViewHolder = new TitleDescriptionViewHolder();
            titleDescriptionViewHolder.title = (TextView)paramView.findViewById(R.id.title_description_list_view_title_label);
            titleDescriptionViewHolder.description = (TextView)paramView.findViewById(R.id.title_description_list_view_description_label);
            paramView.setTag(titleDescriptionViewHolder);
        } else {
            titleDescriptionViewHolder = (TitleDescriptionViewHolder)paramView.getTag();
        }
        TitleDescriptionModel titleDescriptionModel = (TitleDescriptionModel)this.mEntries.get(paramInt);
        if (titleDescriptionModel != null) {
            titleDescriptionViewHolder.title.setText(titleDescriptionModel.getTitle());
            titleDescriptionViewHolder.description.setText(titleDescriptionModel.getDescription());
        }
        return paramView;
    }

    public static class TitleDescriptionViewHolder {
        public TextView description;

        public TextView title;
    }
}

