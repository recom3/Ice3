package com.recom3.snow3.mobilesdk.tripviewer;

import android.util.Log;

import com.recom3.snow3.mobilesdk.engageweb.EngageWebClient;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebClientRequest;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebResponse;
import com.recom3.snow3.mobilesdk.engageweb.IEngageWebClientCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class TripListManager {
    private static final String TAG = "TripListManager";
    private static final String URL_USER_BEST = "/user_bests.json";
    private ITripListQueryCallback mResponseHandler;
    private IEngageWebClientCallback getUserBestHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripListManager.1
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode != 200) {
                TripListManager.this.mResponseHandler.onError(response);
                return;
            }
            try {
                UserBest userBest = new UserBest(new JSONObject(response.mResponseString));
                Log.i(TripListManager.TAG, "successfully got user best");
                TripListManager.this.mResponseHandler.onGotUserBest(userBest);
            } catch (JSONException e) {
                Log.e(TripListManager.TAG, "Error parsing JSON: " + response.mResponseString);
                throw new RuntimeException(e);
            }
        }
    };
    private IEngageWebClientCallback getTripListHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripListManager.2
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode != 200) {
                TripListManager.this.mResponseHandler.onError(response);
                return;
            }
            ArrayList<Trip> tripList = new ArrayList<>();
            try {
                JSONArray ary = new JSONArray(response.mResponseString);
                for (int i = 0; i < ary.length(); i++) {
                    tripList.add(new Trip(ary.getJSONObject(i)));
                }
                Log.i(TripListManager.TAG, "successfully got trip list");
                TripListManager.this.mResponseHandler.onGotTripList(tripList);
            } catch (JSONException e) {
                Log.e(TripListManager.TAG, "Error parsing JSON: " + response.mResponseString);
                throw new RuntimeException(e);
            }
        }
    };
    private IEngageWebClientCallback getTripMetaHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripListManager.3
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode != 200) {
                TripListManager.this.mResponseHandler.onError(response);
                return;
            }
            try {
                JSONObject jsonResult = new JSONObject(response.mResponseString);
                Log.i(TripListManager.TAG, "successfully got trip meta");
                TripListManager.this.mResponseHandler.onGotTripMeta(new TripMeta(jsonResult));
            } catch (JSONException e) {
                Log.e(TripListManager.TAG, "Error parsing JSON: " + response.mResponseString);
                throw new RuntimeException(e);
            }
        }
    };
    private IEngageWebClientCallback getRunHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripListManager.4
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode != 200) {
                TripListManager.this.mResponseHandler.onError(response);
                return;
            }
            try {
                Run run = new Run(new JSONObject(response.mResponseString));
                Log.i(TripListManager.TAG, "successfully got run");
                TripListManager.this.mResponseHandler.onGotRun(run);
            } catch (JSONException e) {
                Log.e(TripListManager.TAG, "Error parsing JSON: " + response.mResponseString);
                throw new RuntimeException(e);
            }
        }
    };
    private IEngageWebClientCallback getAllRunsHandler = new IEngageWebClientCallback() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripListManager.5
        @Override // com.reconinstruments.mobilesdk.engageweb.IEngageWebClientCallback
        public void onConnectionFinished(EngageWebResponse response) {
            if (response.mResponseCode != 200) {
                TripListManager.this.mResponseHandler.onError(response);
                return;
            }
            try {
                JSONObject jobj = new JSONObject(response.mResponseString);
                JSONArray jsonArray = jobj.getJSONArray("TripSegment");
                ArrayList<Run> runs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    runs.add(new Run(jsonArray.getJSONObject(i)));
                }
                Log.i(TripListManager.TAG, "successfully got all runs");
                TripListManager.this.mResponseHandler.onGotAllRuns(runs);
            } catch (JSONException e) {
                Log.e(TripListManager.TAG, "Error parsing JSON: " + response.mResponseString);
                throw new RuntimeException(e);
            }
        }
    };

    public TripListManager(ITripListQueryCallback itl) {
        this.mResponseHandler = itl;
    }

    public EngageWebClientRequest getUserBest() {
        Log.i(TAG, "REQUEST FOR USER BEST");
        UserBest ub = new UserBest();
        ub.best_max_alt = "1000";
        ub.best_max_speed = "90";
        ub.best_total_distance = "10000";
        ub.best_total_jumps = "5";
        ub.sport_type = "1";
        ub.best_total_vertical = "4000";
        this.mResponseHandler.onGotUserBest(ub);
        return null;
    }

    public EngageWebClientRequest getTripList() {
        Log.i(TAG, "REQUEST FOR TRIP LIST");
        EngageWebClient ewc = new EngageWebClient(this.getTripListHandler);
        if(TripService.authSrvc.getUserInfo() != null) {
            String token = TripService.authSrvc.getUserInfo().getAccessToken();
            return ewc.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.GET, TripService.URL_TRIP, token, null);
        }
        else
        {
            return null;
        }
    }

    public EngageWebClientRequest getTripMetaData(String id) {
        Log.i(TAG, "REQUEST FOR TRIP META");
        String path = "/trips/" + id + ".json";
        String token = TripService.authSrvc.getUserInfo().getAccessToken();
        Map<String, String> map = new HashMap<>();
        map.put("metadata", "true");
        EngageWebClient ewc = new EngageWebClient(this.getTripMetaHandler);
        return ewc.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.GET, path, token, map);
    }

    public EngageWebClientRequest getRun(String id, String segment, String frequency) {
        Log.i(TAG, "REQUEST FOR RUN DATA");
        String path = "/trips/" + id + ".json";
        String token = TripService.authSrvc.getUserInfo().getAccessToken();
        EngageWebClient ewc = new EngageWebClient(this.getRunHandler);
        Map<String, String> map = new HashMap<>();
        map.put("metadata", "false");
        map.put("segment", segment);
        if (!frequency.equals("")) {
            map.put("frequency", frequency);
        }
        return ewc.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.GET, path, token, map);
    }

    public EngageWebClientRequest getAllRuns(String id, String frequency) {
        Log.i(TAG, "REQUEST FOR ALL RUNS");
        String path = "/trips/" + id + ".json";
        String token = TripService.authSrvc.getUserInfo().getAccessToken();
        EngageWebClient ewc = new EngageWebClient(this.getAllRunsHandler);
        Map<String, String> map = new HashMap<>();
        map.put("metadata", "false");
        if (!frequency.equals("")) {
            map.put("frequency", frequency);
        }
        return ewc.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.GET, path, token, map);
    }
}