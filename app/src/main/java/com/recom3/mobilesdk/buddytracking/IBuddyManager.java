package com.recom3.mobilesdk.buddytracking;

/**
 * Created by Recom3 on 06/07/2022.
 */

public interface IBuddyManager {
    void onBuddiesUpdated(String paramString);

    void onBuddiesUpdatedError(String paramString);
}
