package com.recom3.snow3.service;

import com.recom3.snow3.mobilesdk.engageweb.EngageWebResponse;
import com.recom3.snow3.mobilesdk.tripviewer.ITripListQueryCallback;
import com.recom3.snow3.mobilesdk.tripviewer.Run;
import com.recom3.snow3.mobilesdk.tripviewer.Trip;
import com.recom3.snow3.mobilesdk.tripviewer.TripMeta;
import com.recom3.snow3.mobilesdk.tripviewer.UserBest;

import java.util.ArrayList;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class TripListHandler implements ITripListQueryCallback {
    @Override // com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
    public void onError(EngageWebResponse arg0) {
    }

    @Override // com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
    public void onGotAllRuns(ArrayList<Run> arg0) {
    }

    @Override // com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
    public void onGotRun(Run arg0) {
    }

    @Override // com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
    public void onGotTripList(ArrayList<Trip> arg0) {
    }

    @Override // com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
    public void onGotTripMeta(TripMeta arg0) {
    }

    @Override // com.reconinstruments.mobilesdk.tripviewer.ITripListQueryCallback
    public void onGotUserBest(UserBest arg0) {
    }
}
