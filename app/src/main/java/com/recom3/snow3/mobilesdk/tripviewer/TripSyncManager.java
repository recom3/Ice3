package com.recom3.snow3.mobilesdk.tripviewer;

import android.util.Base64;
import android.util.Log;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebClient;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebClientRequest;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebResponse;
import com.recom3.snow3.mobilesdk.engageweb.IEngageWebClientCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class TripSyncManager {
    private static final String TAG = "TripSyncManager";
    ITripSyncCallback mResponseHandler;
    public static String downloadTripRequest = "TRIP_SYNC_REQUEST";
    public static String downloadTripFromHUD = "HUD_TRIP_TRANSFER";
    public static String deleteTripRequest = "TRIP_FILE_DELETE_REQUEST";
    public static String successTripRequest = "SUCCESS_TRIP_SYNC";
    public static String failedTripRequest = "FAILED_TRIP_SYNC";
    public static String RETRY_TRIPS_REQUEST_INTENT = "RESTART_TRIP_SYNC";
    public static String mPath = "com.reconinstruments.mobilesdk.tripviewer";
    private IEngageWebClientCallback postTripHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripSyncManager.1
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode == 200) {
                TripSyncManager.this.sendDeleteTripFileRequest();
                Log.i(TripSyncManager.TAG, "successfully posted trip");
                if (TripSyncManager.this.mResponseHandler != null) {
                    TripSyncManager.this.mResponseHandler.onPostedTrip();
                    return;
                }
                return;
            }
            Log.e(TripSyncManager.TAG, "error posting trip, restarting sync request");
            TripSyncManager.this.sendRestartTripSyncRequest();
            if (TripSyncManager.this.mResponseHandler != null) {
                TripSyncManager.this.mResponseHandler.onErrorSyncTrip(response);
            }
        }
    };
    private IEngageWebClientCallback deleteTripHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripSyncManager.2
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode == 200) {
                Log.i(TripSyncManager.TAG, "successfully deleted trip");
                if (TripSyncManager.this.mResponseHandler != null) {
                    TripSyncManager.this.mResponseHandler.onDeletedTrip();
                    return;
                }
                return;
            }
            Log.e(TripSyncManager.TAG, "error deleting trip");
            if (TripSyncManager.this.mResponseHandler != null) {
                TripSyncManager.this.mResponseHandler.onErrorSyncTrip(response);
            }
        }
    };

    public TripSyncManager(ITripSyncCallback its) {
        this.mResponseHandler = its;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void postTrip(File dayFile, File eventFile, File idFile) {
        String encoded_day = "";
        String encoded_event = "";
        String encoded_id = "";
        try {
            encoded_day = gzipAndBase64Encode(readFile(dayFile));
            encoded_event = gzipAndBase64Encode(readFile(eventFile));
            encoded_id = gzipAndBase64Encode(readFile(idFile));
        } catch (FileNotFoundException e2) {
            Log.e(TAG, e2.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        if (TripService.authSrvc.getUserInfo() == null) {
            Log.w(TAG, "User logged out, cancelling HUD trip sync.");
            sendFailedTripSyncRequest();
            return;
        }
        String token = TripService.authSrvc.getUserInfo().getAccessToken();
        EngageWebClient ewc = new EngageWebClient(this.postTripHandler);
        Map<String, String> map = new HashMap<>();
        map.put("id", encoded_id);
        map.put("event", encoded_event);
        map.put("day", encoded_day);
        map.put("gzipped", "true");
        ewc.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.POST, "/trips.json", token, map);
    }

    public void deleteTrip(String id) {
        String path = "/trips/" + id + ".json";
        String token = TripService.authSrvc.getUserInfo().getAccessToken();
        EngageWebClient ewc = new EngageWebClient(this.deleteTripHandler);
        ewc.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.DELETE, path, token, null);
    }

    public void startSyncTripsWithHUD() {
        TripService.syncing = true;
        HUDConnectivityMessage cMsg = new HUDConnectivityMessage();
        cMsg.setIntentFilter(downloadTripRequest);
        cMsg.setRequestKey(0);
        cMsg.setSender(mPath);
        cMsg.setData(writeXmlForDownloadTrip().getBytes());
        if (TripService.hudSrvc != null) {
            TripService.hudSrvc.push(cMsg, HUDConnectivityService.Channel.OBJECT_CHANNEL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDeleteTripFileRequest() {
        HUDConnectivityMessage cMsg = new HUDConnectivityMessage();
        cMsg.setIntentFilter(deleteTripRequest);
        cMsg.setRequestKey(0);
        cMsg.setSender(mPath);
        cMsg.setData(writeXmlForDeleteTrip().getBytes());
        if (TripService.hudSrvc != null) {
            TripService.hudSrvc.push(cMsg, HUDConnectivityService.Channel.OBJECT_CHANNEL);
        }
    }

    private String writeXmlForDeleteTrip() {
        String s = "<recon intent=\"" + deleteTripRequest + "\">";
        String s2 = (s + "<delete dayMD5=\"" + TripService.mLastDayMd5 + "\" eventMD5=\"" + TripService.mLastEventMd5 + "\" dayName=\"" + TripService.mLastDayName + "\" eventName=\"" + TripService.mLastEventName + "\"/>") + "</recon>";
        Log.i(TAG, "delete XML: " + s2);
        return s2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRestartTripSyncRequest() {
        HUDConnectivityMessage cMsg = new HUDConnectivityMessage();
        cMsg.setIntentFilter(RETRY_TRIPS_REQUEST_INTENT);
        cMsg.setRequestKey(0);
        cMsg.setSender(mPath);
        cMsg.setData(writeXmlForRestartTrip().getBytes());
        if (TripService.hudSrvc != null) {
            TripService.hudSrvc.push(cMsg, HUDConnectivityService.Channel.OBJECT_CHANNEL);
        }
    }

    private void sendFailedTripSyncRequest() {
        TripService.syncing = false;
        HUDConnectivityMessage cMsg = new HUDConnectivityMessage();
        cMsg.setIntentFilter(failedTripRequest);
        cMsg.setRequestKey(0);
        cMsg.setSender(mPath);
        cMsg.setData(writeXmlForFailedTrip().getBytes());
        if (TripService.hudSrvc != null) {
            TripService.hudSrvc.push(cMsg, HUDConnectivityService.Channel.OBJECT_CHANNEL);
        }
    }

    private String writeXmlForDownloadTrip() {
        return "<recon intent=\"" + downloadTripRequest + "\"/>";
    }

    private String writeXmlForFailedTrip() {
        return "<recon intent=\"" + failedTripRequest + "\"/>";
    }

    private String writeXmlForRestartTrip() {
        return "<recon intent=\"" + RETRY_TRIPS_REQUEST_INTENT + "\"/>";
    }

    private static byte[] readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }
            byte[] data = new byte[length];
            f.readFully(data);
            Log.i(TAG, "raw file: " + data.length);
            return data;
        } finally {
            f.close();
        }
    }

    private static String gzipAndBase64Encode(byte[] binary) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            DeflaterOutputStream gzin = new DeflaterOutputStream(bs);
            gzin.write(binary);
            gzin.finish();
            bs.close();
            byte[] buffer = bs.toByteArray();
            gzin.close();
            Log.i(TAG, "gzipped length: " + buffer.length);
            byte[] buffer2 = Base64.encode(buffer, 2);
            Log.i(TAG, "base64 length: " + buffer2.length);
            return new String(buffer2);
        } catch (IOException e) {
            Log.i(TAG, "error gzipping data stream", e);
            return null;
        }
    }
}