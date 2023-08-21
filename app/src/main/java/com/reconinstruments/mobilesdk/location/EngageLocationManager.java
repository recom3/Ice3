package com.reconinstruments.mobilesdk.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Printer;
import android.util.StringBuilderPrinter;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationServices;
import com.recom3.connect.messages.LocationMessage;
import com.recom3.connect.messages.LocationRequestMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Recom3 on 05/07/2022.
 */

public class EngageLocationManager implements GoogleApiClient.ConnectionCallbacks, LocationListener{

    public static final String HUD_LOCATION_PROVIDER = "MOD Live";

    private static final String TAG = EngageLocationManager.class.getSimpleName();

    private static int gpsInterval = 30;

    private static int hudInterval = 30;

    private static EngageLocationManager instance;

    private Runnable hudLocationAvailableRunnable = new Runnable() {
        public void run() {
            Log.i(EngageLocationManager.TAG, "HUD location available timer timout! Disabling phone location services.");
            //!
            //EngageLocationManager.this.locationClient.removeLocationUpdates(EngageLocationManager.this);
        }
    };

    private Handler hudLocationHandler = new Handler();

    private Runnable hudLocationWatchdogRunnable = new Runnable() {
        public void run() {
            Log.i(EngageLocationManager.TAG, "HUD location watchdog timer timout! Restarting phone location services.");
            //!
            //EngageLocationManager.access$1002(EngageLocationManager.this, null);
            //EngageLocationManager.access$302(EngageLocationManager.this, false);
            //EngageLocationManager.this.locationClient.requestLocationUpdates(EngageLocationManager.this.locationRequest, EngageLocationManager.this);
        }
    };

    BroadcastReceiver hudReciever = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            if (param1Intent.getAction().equals("RECON_LOCATION_RELAY")) {
                Location location = LocationMessage.parse(param1Intent.getExtras().getString("message"));
                Log.i(EngageLocationManager.TAG, "HUD location received. Accuracy: " + location.getAccuracy());
                EngageLocationManager.this.startHudLocationWatchdogTimer();
                if (EngageLocationManager.this.prevHudLocation == null) {
                    EngageLocationManager.this.startHudLocationAvailableTimer();
                    //!
                    //EngageLocationManager.access$302(EngageLocationManager.this, true);
                }
                //!
                //EngageLocationManager.access$1002(EngageLocationManager.this, location);
                EngageLocationManager.this.onLocationChanged(location);
            }
        }
    };

    private HUDConnectivityService hudService = null;

    private HUDStateUpdateListener hudStateListener = new HUDStateUpdateListener() {
        public void onHUDStateUpdate(HUDStateUpdateListener.HUD_STATE param1HUD_STATE) {
            if (param1HUD_STATE == HUDStateUpdateListener.HUD_STATE.CONNECTED) {
                Log.i(EngageLocationManager.TAG, "HUD connected, starting requesting HUD location...");
                EngageLocationManager.this.requestHudLocationUpdates(EngageLocationManager.hudInterval);
                return;
            }
            Log.i(EngageLocationManager.TAG, "HUD disconnected, (re)starting phone location services");
            //EngageLocationManager.access$302(EngageLocationManager.this, false);
            EngageLocationManager.this.hudLocationHandler.removeCallbacks(EngageLocationManager.this.hudLocationAvailableRunnable);
            EngageLocationManager.this.hudLocationHandler.removeCallbacks(EngageLocationManager.this.hudLocationWatchdogRunnable);
            if (EngageLocationManager.this.locationClient != null && EngageLocationManager.this.locationClient.isConnected())
                //!
                //EngageLocationManager.this.locationClient.requestLocationUpdates(EngageLocationManager.this.locationRequest, EngageLocationManager.this);
                //requestLocationUpdates(GoogleApiClient var1, LocationRequest var2, LocationListener var3);
                LocationServices.FusedLocationApi.requestLocationUpdates(EngageLocationManager.this.locationClient,
                        EngageLocationManager.this.locationRequest, (com.google.android.gms.location.LocationListener) EngageLocationManager.this);
        }
    };

    //private LocationClient locationClient;
    private GoogleApiClient locationClient;

    private Collection<LocationListener> locationListeners = new ArrayList<LocationListener>();

    private LocationRequest locationRequest;

    private Location prevHudLocation = null;

    private boolean receivingHudLocations = false;

    public static EngageLocationManager getInstance() {
        if (instance == null)
            instance = new EngageLocationManager();
        return instance;
    }

    public static boolean isGpsEnabled(Context paramContext) {
        return Settings.Secure.getString(paramContext.getContentResolver(), "location_providers_allowed").contains("gps");
    }

    private void notifyListners(Location paramLocation) {
        Iterator<LocationListener> iterator = this.locationListeners.iterator();
        while (iterator.hasNext())
            ((LocationListener)iterator.next()).onLocationChanged(paramLocation);
    }

    private void requestHudLocationUpdates(int paramInt) {
        Log.i(TAG, "requesting HUD location updates");
        this.prevHudLocation = null;
        this.receivingHudLocations = false;
        LocationRequestMessage.LocationRequest locationRequest = new LocationRequestMessage.LocationRequest(LocationRequestMessage.LocationCommand.ENABLE, paramInt);
        ConnectHelper.broadcastXML((Context)this.hudService, LocationRequestMessage.compose(locationRequest));
    }

    private void requestStopHudLocationUpdates() {
        Log.i(TAG, "requesting STOP HUD location updates");
        LocationRequestMessage.LocationRequest locationRequest = new LocationRequestMessage.LocationRequest(LocationRequestMessage.LocationCommand.DISABLE);
        ConnectHelper.broadcastXML((Context)this.hudService, LocationRequestMessage.compose(locationRequest));
    }

    private void startHudLocationAvailableTimer() {
        this.hudLocationHandler.removeCallbacks(this.hudLocationAvailableRunnable);
        this.hudLocationHandler.postDelayed(this.hudLocationAvailableRunnable, (hudInterval * 4 * 1000));
    }

    private void startHudLocationWatchdogTimer() {
        this.hudLocationHandler.removeCallbacks(this.hudLocationWatchdogRunnable);
        this.hudLocationHandler.postDelayed(this.hudLocationWatchdogRunnable, (hudInterval * 4 * 1000));
    }

    public void addLocationListener(LocationListener paramLocationListener) {
        this.locationListeners.add(paramLocationListener);
    }

    public void disableLocationTracking() {
        if (this.locationClient != null) {
            this.hudLocationHandler.removeCallbacks(this.hudLocationAvailableRunnable);
            this.hudLocationHandler.removeCallbacks(this.hudLocationWatchdogRunnable);
            requestStopHudLocationUpdates();
            this.locationClient.disconnect();
            this.locationClient = null;
            this.locationRequest = null;
            if (this.hudService != null) {
                this.hudService.unregisterReceiver(this.hudReciever);
                this.hudStateListener.stopListening((Context)this.hudService);
                this.hudService = null;
            }
        }
    }

    //public void enableLocationTracking(Context paramContext, GooglePlayServicesClient.OnConnectionFailedListener paramOnConnectionFailedListener, HUDConnectivityService paramHUDConnectivityService) throws IllegalStateException {
    public void enableLocationTracking(Context paramContext, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, HUDConnectivityService paramHUDConnectivityService) throws IllegalStateException {
        int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramContext);
        if (i != 0)
            throw new IllegalStateException(GooglePlayServicesUtil.getErrorString(i));
        this.hudService = paramHUDConnectivityService;
        paramHUDConnectivityService.registerReceiver(this.hudReciever, new IntentFilter("RECON_LOCATION_RELAY"));
        this.hudStateListener.startListening((Context)paramHUDConnectivityService);
        requestHudLocationUpdates(hudInterval);
        this.locationRequest = LocationRequest.create();
        this.locationRequest.setPriority(100);
        this.locationRequest.setInterval((gpsInterval * 1000));
        this.locationRequest.setFastestInterval((gpsInterval * 1000));
        if (this.locationClient == null) {
            //!
            //this.locationClient = new LocationClient(paramContext, instance, paramOnConnectionFailedListener);
            this.locationClient.connect();
            return;
        }
        //!
        //this.locationClient.removeLocationUpdates(this);
        //this.locationClient.requestLocationUpdates(this.locationRequest, this);
    }

    public boolean isLocationTrackingEnabled() {
        return (this.locationClient != null);
    }

    public void onConnected(Bundle paramBundle) {
        Log.i(TAG, "Location services connected, requesting updates");
        //!
        //this.locationClient.requestLocationUpdates(this.locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onDisconnected() {
        Log.w(TAG, "Location services disconnected");
    }

    public void onLocationChanged(Location paramLocation) {
        StringBuilder stringBuilder = new StringBuilder();
        paramLocation.dump((Printer)new StringBuilderPrinter(stringBuilder), "\n");
        Log.i(TAG, stringBuilder.toString());
        if (this.receivingHudLocations) {
            if (paramLocation.getProvider().equals("MOD Live"))
                notifyListners(paramLocation);
            return;
        }
        notifyListners(paramLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void removeLocationListener(LocationListener paramLocationListener) {
        this.locationListeners.remove(paramLocationListener);
    }

    public void setGpsLocationUpdatePeriod(int paramInt) {
        gpsInterval = paramInt;
        if (this.locationRequest != null) {
            this.locationRequest.setInterval((gpsInterval * 1000));
            this.locationRequest.setFastestInterval((gpsInterval * 1000));
        }
        if (this.locationClient != null && this.locationClient.isConnected()) {
            //!
            //this.locationClient.removeLocationUpdates(this);
            //this.locationClient.requestLocationUpdates(this.locationRequest, this);
        }
    }

    public void setHudLocationUpdatePeriod(int paramInt) {
        hudInterval = paramInt;
        if (this.locationClient != null) {
            requestStopHudLocationUpdates();
            requestHudLocationUpdates(hudInterval);
        }
    }
}
