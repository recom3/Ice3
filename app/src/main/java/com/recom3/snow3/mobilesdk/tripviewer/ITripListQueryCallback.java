package com.recom3.snow3.mobilesdk.tripviewer;

import com.recom3.snow3.mobilesdk.engageweb.EngageWebResponse;

import java.util.ArrayList;

/**
 * Created by Recom3 on 28/05/2023.
 */

public interface ITripListQueryCallback {
    void onError(EngageWebResponse engageWebResponse);

    void onGotAllRuns(ArrayList<Run> arrayList);

    void onGotRun(Run run);

    void onGotTripList(ArrayList<Trip> arrayList);

    void onGotTripMeta(TripMeta tripMeta);

    void onGotUserBest(UserBest userBest);
}