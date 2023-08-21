package com.recom3.mobilesdk.buddytracking;

import android.util.Log;

import com.recom3.snow3.mobilesdk.engageweb.EngageWebClient;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebClientRequest;
import com.recom3.snow3.mobilesdk.engageweb.EngageWebResponse;
import com.recom3.snow3.mobilesdk.engageweb.IEngageWebClientCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Recom3 on 06/07/2022.
 */

public class BuddyManager {

    long updated_time = 0L;
    long last_client_time = 0L;

    BuddyHolder mDeltaBuddyHolder = new BuddyHolder();

    BuddyHolder mAllBuddyHolder = new BuddyHolder();

    IBuddyManager mIBManager;

    public BuddyManager(IBuddyManager paramIBuddyManager) {
        this.mIBManager = paramIBuddyManager;
    }

    private IEngageWebClientCallback mUpdateBuddiesListener = new IEngageWebClientCallback() {
        public void onConnectionFinished(EngageWebResponse param1EngageWebResponse) {

            if (param1EngageWebResponse.mResponseCode == 200) {
                BuddyManager.this.updateWithEpochTime(param1EngageWebResponse.mResponseString);
                Log.i("BuddyManager", "buddy update: " + param1EngageWebResponse.mResponseString);
                JSONException jSONException1 = null;
                HashMap<String, Buddy> hashMap = null;
                try {
                    hashMap = BuddyHolder.parseBuddies(param1EngageWebResponse.mResponseString, BuddyManager.this.updated_time);
                } catch (JSONException jSONException) {
                    jSONException.printStackTrace();
                    jSONException = jSONException1;
                }
                BuddyManager.this.mDeltaBuddyHolder = new BuddyHolder((HashMap<String, Buddy>)hashMap);
                BuddyManager.this.mAllBuddyHolder.mergeNewBuddies((HashMap<String, Buddy>)hashMap);
                BuddyManager.this.mAllBuddyHolder.updateBuddiesStatus(BuddyManager.this.updated_time);
                BuddyManager.this.mIBManager.onBuddiesUpdated(BuddyManager.this.mDeltaBuddyHolder.toXml());
                Log.i("BuddyManager", "concluded updating buddies");
                return;
            }
            Log.e("BuddyManager", "error msg: " + ((EngageWebResponse)param1EngageWebResponse).mResponseString);
            BuddyManager.this.mIBManager.onBuddiesUpdatedError("invalid buddy update, response code: " + ((EngageWebResponse)param1EngageWebResponse).mResponseCode);
            Log.e("BuddyManager", "failed updating buddies");

        }
    };

    public void updateBuddies() {
        //if (InternetUtils.isInternetConnected((Context)BuddyService.authSrvc)) {
            Log.i("BuddyManager", "UPDATING BUDDIES REQUEST");
            if(BuddyService.authSrvc!=null) {
                String str = BuddyService.authSrvc.getUserInfo().getAccessToken();
                EngageWebClient engageWebClient = new EngageWebClient(this.mUpdateBuddiesListener);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("delta_since", Long.toString(this.updated_time));
                engageWebClient.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.GET, "/api/meefriendsslocationss", str, hashMap);
            }
        //}
    }

    private void updateWithEpochTime(String paramString) {
        JSONObject jSONObject2 = null;
        if (paramString.equals("") || paramString == null) {
            this.updated_time = 0L;
            Log.e("BuddyManager", "could not retrieve time of buddy update. something went wrong");
            return;
        }
        JSONException jSONException = null;
        try {
            jSONObject2 = new JSONObject(paramString);
            JSONObject jSONObject1 = jSONObject2;
            if (jSONObject1.has("epoch")) {
                try {
                    this.updated_time = Long.parseLong(jSONObject1.getString("epoch"));
                    this.last_client_time = System.currentTimeMillis();
                } catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                } catch (JSONException jSONException1) {
                    jSONException1.printStackTrace();
                }
                return;
            }
        } catch (JSONException jSONException1) {
            jSONException1.printStackTrace();
            jSONException1 = jSONException;
            if (jSONObject2!=null && jSONObject2.has("epoch")) {
                try {
                    this.updated_time = Long.parseLong(jSONObject2.getString("epoch"));
                    this.last_client_time = System.currentTimeMillis();
                } catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                } catch (JSONException jSONException2) {
                    jSONException2.printStackTrace();
                }
                return;
            }
        }
        this.updated_time = 0L;
        Log.e("BuddyManager", "could not retrieve time of buddy update. something went wrong");
    }

    public BuddyHolder getAllBuddies() {
        return this.mAllBuddyHolder;
    }

}
