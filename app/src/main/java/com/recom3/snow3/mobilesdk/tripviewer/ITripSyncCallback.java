package com.recom3.snow3.mobilesdk.tripviewer;

import com.recom3.snow3.mobilesdk.engageweb.EngageWebResponse;

/**
 * Created by Recom3 on 28/05/2023.
 */

public interface ITripSyncCallback {
    void onDeletedTrip();

    void onErrorSyncTrip(EngageWebResponse engageWebResponse);

    void onFinishedSync(boolean z);

    void onPostedTrip();
}
