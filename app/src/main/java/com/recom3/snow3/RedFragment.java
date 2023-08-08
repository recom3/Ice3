package com.recom3.snow3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.recom3.mobilesdk.buddytracking.Buddy;
import com.recom3.mobilesdk.buddytracking.BuddyAirwaveService;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.util.validation.ICallback;
import com.reconinstruments.mobilesdk.location.EngageLocationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Recom3 on 05/07/2022.
 * Fragment to hold buddies
 */

public class RedFragment extends Fragment {

    ListView l;
    Button b;
    String tutorials[]
            = { "Algorithms", "Data Structures",
            "Languages", "Interview Corner",
            "GATE", "ISRO CS",
            "UGC NET CS", "CS Subjects",
            "Web Technologies" };

    private String mBuddiesOnlineStringHeader;
    private String mBuddiesPendingRequestString;

    public static BuddyAirwaveService mBuddyAirwaveService = null;

    //private PullToRefreshAttacher mPullToRefreshAttacher;

    private boolean mBuddyAirwaveServiceIsBound;

    private BuddyEnableCallback mBuddyActivateCallback;

    Context m_context;

    //Avoid non-default constructors in fragments: use a default constructor plus Fragment#setArguments(Bundle) instead less... (Ctrl+F1)
    //From the Fragment documentation: Every fragment must have an empty constructor, so it can be instantiated when restoring its activity's state. It is strongly recommended that subclasses do not have other constructors with parameters, since these constructors will not be called when the fragment is re-instantiated; instead, arguments can be supplied by the caller with setArguments(Bundle) and later retrieved by the Fragment with getArguments().  More info: http://developer.android.com/reference/android/app/Fragment.html#Fragment()
    @SuppressLint("ValidFragment")
    public RedFragment(BuddyEnableCallback buddyActivateCallback)
    {
        mBuddyActivateCallback = buddyActivateCallback;
    }

    public RedFragment()
    {

    }

    void setContext(Context context)
    {
        m_context = context;
    }

    private ServiceConnection buddyServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {

            //!recom3
            //startService(BuddyAirwaveService.class);
            mBuddyAirwaveService = (BuddyAirwaveService)((EngageSdkService.LocalBinder)param1IBinder).getService();
            mBuddyAirwaveServiceIsBound = true;
            mBuddyAirwaveService.bindDependentServices(new Handler.Callback() {
                public boolean handleMessage(Message param2Message) {
                    return false;
                }
            });
            //Log.i(TAG,"BuddyAirwaveService Connected!");
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            mBuddyAirwaveService = null;
            //Log.i(TAG,"BuddyAirwaveService Disconnected!");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mBuddiesPendingRequestString = "xxxx";//getString(2131230780);
        this.mBuddiesOnlineStringHeader = "xxxx";//getString(2131230779);

        View rootView = inflater.inflate(R.layout.view_red, container, false);

        l = rootView.findViewById(R.id.list);
        b = rootView.findViewById(R.id.buddies_layout_button_buddy_tracking);

        ArrayAdapter<String> arr;
        arr = new ArrayAdapter<String>(
                rootView.getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                tutorials);
        l.setAdapter(arr);

        rootView.findViewById(R.id.buddies_layout_button_buddy_tracking)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        l.setVisibility(View.VISIBLE);
                        b.setVisibility(View.INVISIBLE);

                        if(mBuddyAirwaveService != null) {
                            mBuddyAirwaveService.setOnBuddiesUpdatedCallback(RedFragment.this.mOnBuddiesUpdatedCallback);
                        }

                        //this.mCheckableGooglePlayService.checkGooglePlayServicesAvailable(new ICallback() {
                        //    public void execute() {
                                RedFragment.this.enableLocationTracking();
                        //    }
                        //});
                    }
                });

        // Demonstration of a collection-browsing activity.
        /*
        rootView.findViewById(R.id.button1)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Test.class);
                        startActivity(intent);
                    }
                });

        */

        doBindService(rootView.getContext());

        return rootView;
    }

    private void doBindService(Context context) {
        if(mBuddyAirwaveService==null) {
            //bindService((Service)mBuddyAirwaveService, BuddyAirwaveService.class, this.buddyServiceConnection);
            Intent intent = new Intent(context, BuddyAirwaveService.class);
            context.bindService(intent, this.buddyServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ICallback mOnBuddiesUpdatedCallback = new ICallback() {
        public void execute() {
            int here;
            here = 1;
            loadBuddiesList();
            //mPullToRefreshAttacher.setRefreshComplete();
        }
    };

    private void loadBuddiesList() {
        //this.mBuddiesWithHeaderResource = createBuddiesWithHeader(mBuddyAirwaveService.getBuddyManager().getAllBuddies().getBuddies());
        //createAdapter();
    }

    private Map<String, ArrayList<Buddy>> createBuddiesWithHeader(HashMap<String, Buddy> paramHashMap) {
        ArrayList<Buddy> arrayList1 = new ArrayList();
        ArrayList<Buddy> arrayList2 = new ArrayList();
        ArrayList<Buddy> arrayList3 = new ArrayList();
        Iterator<String> iterator = paramHashMap.keySet().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                paramHashMap = new HashMap<String, Buddy>();
                //!recom3
                //if (!arrayList1.isEmpty())
                //    paramHashMap.put(getString(2131230779), arrayList1);
                //if (!arrayList2.isEmpty())
                //    paramHashMap.put(getString(2131230778), arrayList2);
                //if (!arrayList3.isEmpty())
                //    paramHashMap.put(this.mBuddiesPendingRequestString, arrayList3);
                return sortByComparator((Map)paramHashMap);
            }
            Buddy buddy = paramHashMap.get(iterator.next());
            if (Buddy.BuddyState.ACCEPTED == buddy.getBuddyState()) {
                if (Buddy.UserState.ONLINE == buddy.getState())
                    arrayList1.add(buddy);
                if (Buddy.UserState.OFFLINE == buddy.getState())
                    arrayList2.add(buddy);
            }
            if (Buddy.BuddyState.REQUESTED == buddy.getBuddyState() || Buddy.BuddyState.REQUESTING == buddy.getBuddyState())
                arrayList3.add(buddy);
        }
    }

    private Map<String, ArrayList<Buddy>> sortByComparator(Map<String, ArrayList<Buddy>> paramMap) {
        LinkedList<?> linkedList = new LinkedList(paramMap.keySet());
        //!!!
        Collections.sort(linkedList, new Comparator<Object>() {
            @Override
            public int compare(Object param1String1, Object o2) {
                return param1String1.equals(mBuddiesOnlineStringHeader) ? -1 : (param1String1.equals(mBuddiesPendingRequestString) ? 0 : 1);
            }
        });
        /*
        Collections.sort(linkedList, new Comparator<String>() {
            public int compare(String param1String1, String param1String2) {
                return param1String1.equals(mBuddiesOnlineStringHeader) ? -1 : (param1String1.equals(mBuddiesPendingRequestString) ? 0 : 1);
            }
        });
        */
        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<Object, Object>();
        Iterator<?> iterator = linkedList.iterator();
        while (true) {
            if (!iterator.hasNext())
                return (Map)linkedHashMap;
            String str = (String)iterator.next();
            linkedHashMap.put(str, paramMap.get(str));
        }
    }

    private void enableLocationTracking() {
        /*
        //It still missing bind in MainActivity
        EngageLocationManager.getInstance().enableLocationTracking(
                (Context)getActivity(),
                new GoogleApiClient.OnConnectionFailedListener() {
                    public void onConnectionFailed(ConnectionResult param1ConnectionResult) {
                        if (param1ConnectionResult.hasResolution())
                        try {
                            //param1ConnectionResult.startResolutionForResult((Activity)this.getActivity(), -102);
                            param1ConnectionResult.startResolutionForResult((Activity)RedFragment.this.getContext(), -102);
                            return;
                        } catch (android.content.IntentSender.SendIntentException sendIntentException) {
                            throw new RuntimeException(sendIntentException);
                        }
                    }
                },(HUDConnectivityService)MainActivity.mConnectivityHudService);
        */

        if(mBuddyAirwaveService!=null) {
            mBuddyAirwaveService.enableBuddyTracking();
            mBuddyActivateCallback.onBuddyTrackingEnabled(true);
        }
    }

    private void createAdapter() {
        //this.mBuddyItemArrayAdapter = new BuddyItemArrayAdapter((Activity)getActivity(), 2131165285, this.mBuddiesWithHeaderResource);
        //this.mListView.setAdapter((ListAdapter)this.mBuddyItemArrayAdapter);
    }

    public void showBuddiesListView(View v) {
        //addFragment(R.id.buddies_layout_center_container_fragment, new BuddiesListFragment());
        //!!!
        //this.mAirwaveService.setBuddyTracking(true);

        if(mBuddyAirwaveService!=null) {
            mBuddyAirwaveService.enableBuddyTracking();
        }
    }

}
