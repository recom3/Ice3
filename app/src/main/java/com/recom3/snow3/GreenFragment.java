package com.recom3.snow3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.recom3.mobilesdk.buddytracking.BuddyAirwaveService;
import com.recom3.snow3.activity.buddy.BuddiesTabFragment;
import com.recom3.snow3.activity.trip.TripCursorAdapter;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.tripviewer.ITripListQueryCallback;
import com.recom3.snow3.mobilesdk.tripviewer.Trip;
import com.recom3.snow3.service.TripAirwaveService;
import com.recom3.snow3.service.TripListHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Recom3 on 05/07/2022.
 */

public class GreenFragment extends Fragment {

    private static final String TAG = GreenFragment.class.getSimpleName();
    private TripAirwaveService mTripAirwaveService = null;
    private boolean mTripAirwaveServiceIsBound;
    ListView mListView;

    private final ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    private ServiceConnection tripsServiceConnection = new ServiceConnection() { // from class: com.oakley.snow.activity.trip.GreenFragment.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            GreenFragment.this.startService(TripAirwaveService.class);
            GreenFragment.this.mTripAirwaveService = (TripAirwaveService) ((EngageSdkService.LocalBinder) service).getService();
            GreenFragment.this.mTripAirwaveServiceIsBound = true;
            GreenFragment.this.mTripAirwaveService.bindDependentServices(new Handler.Callback() {
                @Override // android.os.Handler.Callback
                public boolean handleMessage(Message msg) {
                    GreenFragment.this.loadTripsList();
                    return false;
                }
            });
            Log.i(TAG, "TripAirwaveService Connected!");
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            GreenFragment.this.mTripAirwaveService = null;
            Log.i(TAG, "TripAirwaveService Disconnected!");
        }
    };

    public void startService(Class<?> cl) {
        //this.mStartActivityDelegate.startService(cl);
    }

    private void doBindService(Context context) {
        if(mTripAirwaveService==null) {
            Intent intent = new Intent(context, TripAirwaveService.class);
            context.bindService(intent, this.tripsServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ImageView imageView = (ImageView) getView().findViewById(R.id.foo);
        // or  (ImageView) view.findViewById(R.id.foo);
        ListView myView = (ListView) getView().findViewById(R.id.trips_list_layout_list_view);
        mListView = myView;

        if(this.mListView!=null) {
            this.mListView.setOnItemClickListener(this.onItemClickListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_green, container, false);

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

    public void loadTripsList() {
        this.mTripAirwaveService.newTripListManager(this.tripListHandler);
        this.mTripAirwaveService.getTripListManager().getTripList();
    }

    private ITripListQueryCallback tripListHandler = new TripListHandler() { // from class: com.oakley.snow.activity.trip.TripsTabFragment.2
        @Override // com.oakley.snow.service.trip.TripListHandler, com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
        public void onGotTripList(ArrayList<Trip> trips) {
            //!recom3
            //HashMap<String, ArrayList<Trip>> tripsWithHeader = GreenFragment.this.createTripListWithHeaders(trips);

            //!recom3
            //TripItemArrayAdapter tripItemArrayAdapter = new TripItemArrayAdapter(TripsTabFragment.this.getActivity(), R.id.trips_list_layout_list_view, tripsWithHeader, TripsTabFragment.this.mAirwaveService);
            //TripsTabFragment.this.mListView.setAdapter((ListAdapter) tripItemArrayAdapter);
            //TripsTabFragment.this.mPullToRefreshAttacher.setRefreshComplete();

            TripCursorAdapter adapter = new TripCursorAdapter(trips, mTripAirwaveService);
            GreenFragment.this.mListView.setAdapter(adapter);
        }
    };

    /*
    public HashMap<String, ArrayList<Trip>> createTripListWithHeaders(ArrayList<Trip> trips) {
        HashMap<String, ArrayList<Trip>> hashMap = new HashMap<>();
        Iterator<Trip> it = trips.iterator();
        while (it.hasNext()) {
            Trip trip = it.next();
            setValuesForTripDetails(trip);
            String header = getHeaderDateFormated(trip);
            if (!hashMap.containsKey(header)) {
                ArrayList<Trip> tripsForHeader = new ArrayList<>();
                tripsForHeader.add(trip);
                hashMap.put(header, tripsForHeader);
            } else {
                hashMap.get(header).add(trip);
            }
        }
        return hashMap;
    }
    */

    //public void onRefreshStarted(View arg0) {
    //    loadTripsList();
    //}

}
