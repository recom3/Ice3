package com.recom3.snow3.repository;

import android.content.Context;

import com.google.inject.Inject;

/**
 * Created by Recom3 on 25/01/2022.
 */

//@ContextSingleton
public class AirwaveRepository extends AbstractRepository {
    private static final String AIRWAVE_REPOSITORY = "AirwaveRepository";

    private static final String FIRST_TIME_USING_AIRWAVE = "FIRST_TIME_USING_AIRWAVE";

    private static final String IS_BUDDY_TRACKING = "IS_BUDDY_TRACKING";

    private static final String IS_MILES_METRIC = "IS_MILES_METRIC";

    @Inject
    public AirwaveRepository(Context paramContext) {
        super(paramContext);
    }

    protected String getSharedPreferencesName() {
        return "AirwaveRepository";
    }

    public boolean isBuddyTracking() {
        return getSharedPreferences().getBoolean("IS_BUDDY_TRACKING", false);
    }

    public boolean isFirstTimeUsingAirwave() {
        return getSharedPreferences().getBoolean("FIRST_TIME_USING_AIRWAVE", true);
    }

    public boolean isMilesMetric() {
        return getSharedPreferences().getBoolean("IS_MILES_METRIC", false);
    }

    public void saveFirstTimeUsingAirwave() {
        saveBoolean("FIRST_TIME_USING_AIRWAVE", Boolean.valueOf(false));
    }

    public void setBuddyTracking(Boolean paramBoolean) {
        saveBoolean("IS_BUDDY_TRACKING", paramBoolean);
    }

    public void setMilesAsMetric(Boolean paramBoolean) {
        saveBoolean("IS_MILES_METRIC", paramBoolean);
    }
}
