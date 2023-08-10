package com.recom3.mobilesdk.buddytracking;

import com.recom3.jetandroid.services.EngageHudConnectivityService;
import com.recom3.snow3.service.AuthenticationAirwaveService;
import com.recom3.snow3.util.validation.ICallback;

/**
 * Created by Recom3 on 06/07/2022.
 */

public class BuddyAirwaveService extends BuddyService {

    private ICallback mCallback;

    public BuddyAirwaveService() {
        super(AuthenticationAirwaveService.class,
                //ConnectivityHudService.class);
                EngageHudConnectivityService.class);
    }

    public void disableBuddyTracking() {
        setUpdateFromWebPeriod(-1);
        //setUpdateToWebPeriod(-1);
    }

    public void enableBuddyTracking() {
        setUpdateFromWebPeriod(15);
        //setUpdateToWebPeriod(15);
    }

    public void onBuddiesUpdated(String paramString) {
        super.onBuddiesUpdated(paramString);
        if (this.mCallback != null)
            this.mCallback.execute();
    }

    public void setOnBuddiesUpdatedCallback(ICallback paramICallback) {
        this.mCallback = paramICallback;
    }
}
