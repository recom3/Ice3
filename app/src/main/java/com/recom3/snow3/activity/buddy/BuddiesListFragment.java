package com.recom3.snow3.activity.buddy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.recom3.mobilesdk.buddytracking.Buddy;
import com.recom3.mobilesdk.buddytracking.BuddyAirwaveService;
import com.recom3.mobilesdk.buddytracking.BuddyHolder;
import com.recom3.mobilesdk.buddytracking.BuddyManager;
import com.recom3.snow3.R;
import com.recom3.snow3.util.validation.ICallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Recom3 on 25/05/2023.
 */

public class BuddiesListFragment extends Fragment {

    private Map<String, ArrayList<Buddy>> mBuddiesWithHeaderResource;
    private String mBuddiesOnlineStringHeader;
    private String mBuddiesPendingRequestString;
    //private PullToRefreshAttacher mPullToRefreshAttacher;
    private BuddyItemArrayAdapter mBuddyItemArrayAdapter;
    ListView mListView;

    private final ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ImageView imageView = (ImageView) getView().findViewById(R.id.foo);
        // or  (ImageView) view.findViewById(R.id.foo);
        ListView myView = (ListView) getView().findViewById(R.id.trips_list_layout_list_view);
        mListView = myView;

        if(this.mListView!=null) {
            this.mListView.setOnItemClickListener(this.onItemClickListener);
        }

        if(BuddiesTabFragment.mBuddyAirwaveService!=null) {
            BuddiesTabFragment.mBuddyAirwaveService.setOnBuddiesUpdatedCallback(this.mOnBuddiesUpdatedCallback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBuddiesPendingRequestString = getString(R.string.buddies_pending_request);
        this.mBuddiesOnlineStringHeader = getString(R.string.buddies_online);

        View rootView = inflater.inflate(R.layout.buddies_list_fragment, container, false);

        this.mBuddiesPendingRequestString = getString(R.string.buddies_pending_request);
        this.mBuddiesOnlineStringHeader = getString(R.string.buddies_online);

        /*
        this.mCheckableGooglePlayService.checkGooglePlayServicesAvailable(new ICallback() { // from class: com.oakley.snow.activity.buddy.BuddiesListFragment.3
            @Override // com.sook.android.utility.ICallback
            public void execute() {
                BuddiesListFragment.this.enableLocationTracking();
            }
        });
        */

        /*
        this.mPullToRefreshAttacher = ((MainViewPagerActivity) getActivity()).getPullToRefreshAttacher();
        this.mPullToRefreshAttacher.addRefreshableView(this.mListView, this);
        this.mPullToRefreshAttacher.setRefreshing(true);
        */

        return rootView;
    }

    private ICallback mOnBuddiesUpdatedCallback = new ICallback() { // from class: com.oakley.snow.activity.buddy.BuddiesListFragment.2
        @Override // com.sook.android.utility.ICallback
        public void execute() {
            BuddiesListFragment.this.loadBuddiesList();
            //BuddiesListFragment.this.mPullToRefreshAttacher.setRefreshComplete();
        }
    };

    public void loadBuddiesList() {
        HashMap<String, Buddy> buddies = BuddiesTabFragment.mBuddyAirwaveService.getBuddyManager().getAllBuddies().getBuddies();
        this.mBuddiesWithHeaderResource = createBuddiesWithHeader(buddies);
        //!!!
        //createAdapter();
        BuddyCursorAdapter adapter = new BuddyCursorAdapter(buddies);
        this.mListView.setAdapter(adapter);
    }

    private Map<String, ArrayList<Buddy>> createBuddiesWithHeader(HashMap<String, Buddy> buddies) {
        ArrayList<Buddy> buddiesOnlineList = new ArrayList<>();
        ArrayList<Buddy> buddiesOfflineList = new ArrayList<>();
        ArrayList<Buddy> buddiesPendinglineList = new ArrayList<>();
        for (String keyElement : buddies.keySet()) {
            Buddy buddy = buddies.get(keyElement);
            if (Buddy.BuddyState.ACCEPTED == buddy.getBuddyState()) {
                if (Buddy.UserState.ONLINE == buddy.getState()) {
                    buddiesOnlineList.add(buddy);
                }
                if (Buddy.UserState.OFFLINE == buddy.getState()) {
                    buddiesOfflineList.add(buddy);
                }
            }
            if (Buddy.BuddyState.REQUESTED == buddy.getBuddyState() || Buddy.BuddyState.REQUESTING == buddy.getBuddyState()) {
                buddiesPendinglineList.add(buddy);
            }
        }
        HashMap<String, ArrayList<Buddy>> buddiesWithHeader = new HashMap<>();
        if (!buddiesOnlineList.isEmpty()) {
            buddiesWithHeader.put(getString(R.string.buddies_online), buddiesOnlineList);
        }
        if (!buddiesOfflineList.isEmpty()) {
            buddiesWithHeader.put(getString(R.string.buddies_offline), buddiesOfflineList);
        }
        if (!buddiesPendinglineList.isEmpty()) {
            buddiesWithHeader.put(this.mBuddiesPendingRequestString, buddiesPendinglineList);
        }
        return sortByComparator(buddiesWithHeader);
    }

    private Map<String, ArrayList<Buddy>> sortByComparator(Map<String, ArrayList<Buddy>> unsortMap) {
        List<String> list = new LinkedList<>(unsortMap.keySet());
        Collections.sort(list, new Comparator<String>() { // from class: com.oakley.snow.activity.buddy.BuddiesListFragment.5
            @Override // java.util.Comparator
            public int compare(String lhs, String rhs) {
                if (!lhs.equals(BuddiesListFragment.this.mBuddiesOnlineStringHeader)) {
                    if (lhs.equals(BuddiesListFragment.this.mBuddiesPendingRequestString)) {
                        return 0;
                    }
                    return 1;
                }
                return -1;
            }
        });
        Map<String, ArrayList<Buddy>> sortedMap = new LinkedHashMap<>();
        for (String next : list) {
            sortedMap.put(next, unsortMap.get(next));
        }
        return sortedMap;
    }

    //@Override // uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener
    public void onRefreshStarted(View arg0) {
        BuddiesTabFragment.mBuddyAirwaveService.getBuddyManager().updateBuddies();
    }

    private void loadBuddiesList2() {
        BuddyAirwaveService r1 = BuddiesTabFragment.mBuddyAirwaveService;
        BuddyManager r2 = r1.getBuddyManager();
        BuddyHolder r3 = r2.getAllBuddies();
        java.util.HashMap r0 = r3.getBuddies();
        java.util.Map map = this.createBuddiesWithHeader(r0);

        mBuddiesWithHeaderResource = map;
        createAdapter();
    }

    public void createAdapter() {
        this.mBuddyItemArrayAdapter = new BuddyItemArrayAdapter(getActivity(), R.id.trips_list_layout_list_view, this.mBuddiesWithHeaderResource);
        this.mListView.setAdapter((ListAdapter) this.mBuddyItemArrayAdapter);
    }

    private void createAdapter2() {
        /*
        BuddyItemArrayAdapter r0 = new BuddyItemArrayAdapter();
        FragmentActivity r1 = this.getActivity();
        //r2 = 2131165285(0x7f070065, float:1.7944783E38)
        Map<String, ArrayList<Buddy>> r3 = mBuddiesWithHeaderResource;
        //r0.<init>(r1, r2, r3);
        r4.mBuddyItemArrayAdapter = r0
        PinnedHeaderListView r0 = this.mListView;
        BuddyItemArrayAdapter r1 = this.mBuddyItemArrayAdapter;
        r0.setAdapter(r1)
        return
        */
    }

}

