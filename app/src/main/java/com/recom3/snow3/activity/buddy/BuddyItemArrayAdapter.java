package com.recom3.snow3.activity.buddy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recom3.mobilesdk.buddytracking.Buddy;
import com.recom3.snow3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Recom3 on 27/05/2023.
 */

public class BuddyItemArrayAdapter{//extends SectionedBaseAdapter {
    private Activity mActivity;
    private Map<String, ArrayList<Buddy>> mEntries;
    private Object[] mKeys;
    private HashMap<String, String> mSubTitlePendingRequestHeader = new HashMap<>();

    /* loaded from: classes.dex */
    public static class TitleWithSubtitleItemViewHolder {
        public TextView subtitle;
        public TextView title;
    }

    public BuddyItemArrayAdapter(Activity activity, int textViewResourceId, Map<String, ArrayList<Buddy>> entries) {
        this.mKeys = entries.keySet().toArray();
        this.mEntries = entries;
        this.mActivity = activity;
        this.mSubTitlePendingRequestHeader.put(Buddy.BuddyState.REQUESTED.name(), this.mActivity.getString(R.string.buddies_pending_request_requesting));
        this.mSubTitlePendingRequestHeader.put(Buddy.BuddyState.REQUESTING.name(), this.mActivity.getString(R.string.buddies_pending_request_requested));
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
        TitleWithSubtitleItemViewHolder titleWithSubtitleItemHolder;
        View view = convertView;
        if (view == null) {
            LayoutInflater viewInflate = LayoutInflater.from(this.mActivity);
            view = viewInflate.inflate(R.layout.title_subtilte_listview_fragment, (ViewGroup) null);
            titleWithSubtitleItemHolder = new TitleWithSubtitleItemViewHolder();
            titleWithSubtitleItemHolder.title = (TextView) view.findViewById(R.id.title_subtitle_item_fragment_title_text_view);
            titleWithSubtitleItemHolder.subtitle = (TextView) view.findViewById(R.id.title_subtitle_item_fragment_subtitle_text_view);
            view.setTag(titleWithSubtitleItemHolder);
        } else {
            titleWithSubtitleItemHolder = (TitleWithSubtitleItemViewHolder) view.getTag();
        }
        Buddy buddy = this.mEntries.get(this.mKeys[section]).get(position);
        if (buddy != null) {
            titleWithSubtitleItemHolder.title.setText(buddy.getName());
            if (Buddy.BuddyState.ACCEPTED == buddy.getBuddyState()) {
                titleWithSubtitleItemHolder.subtitle.setText(buddy.getState().name());
            } else {
                titleWithSubtitleItemHolder.subtitle.setText(this.mSubTitlePendingRequestHeader.get(buddy.getBuddyState().name()));
            }
        }
        return view;
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
        CharSequence header = (CharSequence) this.mKeys[section];
        ((TextView) layout.findViewById(R.id.trips_list_header_layout_text)).setText(header);
        return layout;
    }
}
