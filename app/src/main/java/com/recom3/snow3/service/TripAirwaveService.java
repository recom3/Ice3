package com.recom3.snow3.service;

import com.recom3.jetandroid.services.EngageHudConnectivityService;
import com.recom3.snow3.mobilesdk.tripviewer.TripService;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class TripAirwaveService extends TripService {
    public TripAirwaveService() {
        super(AuthenticationAirwaveService.class,
                //ConnectivityHudService.class);
                EngageHudConnectivityService.class);
    }
}
