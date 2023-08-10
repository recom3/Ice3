package com.reconinstruments.mobilesdk.agps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;

/**
 * Created by Recom3 on 18/07/2023.
 */

public class AGpsModule implements LocationListener {

    public static final String TAG = AGpsModule.class.getSimpleName();

    public static Context context;
    private LocationManager e;
    private int upDatePeriod = -1;//d?

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //c
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals("com.reconinstruments.mobilesdk.agps.tophone.SET_AGPS_LOCATION_UPDATE_PERIOD")) {
                if (!intent.getAction().equals("RECON_LOCATION_RELAY") || AGpsModule.this.upDatePeriod == -1) {
                    return;
                }
                Log.i(AGpsModule.TAG, "Received a HUD location, disabling assisted locations");
                AGpsModule.setUpdatePeriod(AGpsModule.this, -1);
                return;
            }
            Log.i(AGpsModule.TAG, "ReconAGps.LOCATION_UPDATE_PERIOD_INTENT received");
            byte[] byteArrayExtra = intent.getByteArrayExtra("message");
            if (byteArrayExtra == null) {
                return;
            }
            try {
                int iPeriod = ReconAGps.getUpdatePeriod(new HUDConnectivityMessage(byteArrayExtra));
                if (iPeriod != AGpsModule.this.upDatePeriod) {
                    AGpsModule.setUpdatePeriod(AGpsModule.this, iPeriod);
                }
                Log.i(AGpsModule.TAG, "update period is " + iPeriod);
            } catch (ReconAGps.InvalidUpdatePeriodXml e) {
                Log.e(AGpsModule.TAG, "Exception: ", e);
            }
        }
    };

    public AGpsModule(Context context) {
        this.context = context.getApplicationContext();
        this.e = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        Log.i(TAG, "AGpsModule created");
    }

    static void setUpdatePeriod(AGpsModule aGpsModule, int sec) {
        aGpsModule.upDatePeriod = sec;
        if (sec == -1) {
            aGpsModule.a();
        } else {
            //recom3 added to check permission
            //context have been made static: can this cause problems?
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "ActivityCompat.checkSelfPermission failed.");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            aGpsModule.e.requestLocationUpdates("network", sec * 1000, 5.0f, aGpsModule);
        }
    }

    public final void a() {
        this.upDatePeriod = -1;
        this.e.removeUpdates(this);
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged()");
        if (this.upDatePeriod != -1) {
            if (location == null || location.getAccuracy() >= 50000.0f) {
                Log.i(TAG, "Location is not accurate enough to report to HUD");
                return;
            }
            ReconAGps.broadcastLocation(this.context, location);
            Log.i(TAG, "Location sent to HUD");
        }
    }

    @Override // android.location.LocationListener
    public void onProviderDisabled(String str) {
        Log.i(TAG, "onProviderDisabled(" + str + ")");
    }

    @Override // android.location.LocationListener
    public void onProviderEnabled(String str) {
        Log.i(TAG, "onProviderEnabled(" + str + ")");
    }

    @Override // android.location.LocationListener
    public void onStatusChanged(String str, int i, Bundle bundle) {
        Log.i(TAG, "onStatusChanged(" + str + "," + i + ")");
    }
}
