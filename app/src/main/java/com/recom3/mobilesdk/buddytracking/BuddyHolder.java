package com.recom3.mobilesdk.buddytracking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Recom3 on 06/07/2022.
 */

public class BuddyHolder {
    private static final String TAG = "BuddyHolder";

    private static long offlineThreshold = 300L;

    private HashMap<String, Buddy> mBuddies = null;

    public BuddyHolder() {
        this.mBuddies = null;
    }

    public BuddyHolder(HashMap<String, Buddy> paramHashMap) {
        this.mBuddies = paramHashMap;
    }

    static HashMap<String, Buddy> parseBuddies(String paramString, long paramLong) throws JSONException {
        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        if (paramString != null) {
            JSONObject jSONObject = new JSONObject(paramString);
            if (jSONObject.has("Friend")) {
                JSONArray jSONArray = jSONObject.getJSONArray("Friend");
                for (byte b = 0; b < jSONArray.length(); b++) {
                    Buddy buddy = new Buddy(jSONArray.getJSONObject(b));
                    buddy.setBuddyState(Buddy.BuddyState.ACCEPTED);
                    if (buddy.getLocation() != null) {
                        if (paramLong - buddy.getLocation().getTime() / 1000L < offlineThreshold) {
                            buddy.changeState(Buddy.UserState.ONLINE);
                        } else {
                            buddy.changeState(Buddy.UserState.OFFLINE);
                        }
                    } else {
                        buddy.changeState(Buddy.UserState.OFFLINE);
                    }
                    hashMap.put(buddy.getID(), buddy);
                }
            }
            if (jSONObject.has("FriendRequesting")) {
                JSONArray jSONArray = jSONObject.getJSONArray("FriendRequesting");
                for (byte b = 0; b < jSONArray.length(); b++) {
                    Buddy buddy = new Buddy(jSONArray.getJSONObject(b));
                    buddy.setBuddyState(Buddy.BuddyState.REQUESTING);
                    hashMap.put(buddy.getID(), buddy);
                }
            }
            if (jSONObject.has("FriendRequest")) {
                JSONArray jSONArray = jSONObject.getJSONArray("FriendRequest");
                for (byte b = 0; b < jSONArray.length(); b++) {
                    Buddy buddy = new Buddy(jSONArray.getJSONObject(b));
                    buddy.setBuddyState(Buddy.BuddyState.REQUESTED);
                    hashMap.put(buddy.getID(), buddy);
                }
            }
            if (jSONObject.has("FriendRemoved")) {
                JSONArray jSONArray = jSONObject.getJSONArray("FriendRemoved");
                for (byte b = 0; b < jSONArray.length(); b++) {
                    Buddy buddy = new Buddy(jSONArray.getJSONObject(b));
                    buddy.setBuddyState(Buddy.BuddyState.REMOVED);
                    hashMap.put(buddy.getID(), buddy);
                }
            }
            if (jSONObject.has("FriendRejected")) {
                JSONArray jSONArray = jSONObject.getJSONArray("FriendRejected");
                byte b = 0;
                while (true) {
                    if (b < jSONArray.length()) {
                        Buddy buddy = new Buddy(jSONArray.getJSONObject(b));
                        buddy.setBuddyState(Buddy.BuddyState.REJECTED);
                        hashMap.put(buddy.getID(), buddy);
                        b++;
                        continue;
                    }
                    return (HashMap)hashMap;
                }
            }
        }
        return (HashMap)hashMap;
    }

    public HashMap<String, Buddy> getBuddies() {
        return this.mBuddies;
    }

    public Buddy getBuddy(String paramString) {
        return this.mBuddies.get(paramString);
    }

    void mergeNewBuddies(HashMap<String, Buddy> paramHashMap) {
        if (this.mBuddies != null) {
            if (paramHashMap != null)
                for (String str : paramHashMap.keySet()) {
                    if ((Buddy)this.mBuddies.get(str) != null) {
                        ((Buddy)this.mBuddies.get(str)).merge(paramHashMap.get(str));
                        continue;
                    }
                    this.mBuddies.put(str, paramHashMap.get(str));
                }
        } else {
            this.mBuddies = paramHashMap;
        }
    }

    public void setBuddies(HashMap<String, Buddy> paramHashMap) {
        this.mBuddies = paramHashMap;
    }

    ArrayList<Buddy> toArray() {
        ArrayList<Buddy> arrayList = new ArrayList();
        if (this.mBuddies != null)
            for (String str : this.mBuddies.keySet())
                arrayList.add(this.mBuddies.get(str));
        return arrayList;
    }

    public String toXml() {
        String str1 = "";
        String str2 = str1;
        if (this.mBuddies != null) {
            Iterator<String> iterator = this.mBuddies.keySet().iterator();
            while (true) {
                str2 = str1;
                if (iterator.hasNext()) {
                    str2 = iterator.next();
                    str1 = str1 + ((Buddy)this.mBuddies.get(str2)).toString();
                    continue;
                }
                break;
            }
        }
        return str2;
    }

    public void updateBuddiesStatus(long paramLong) {
        if (this.mBuddies != null) {
            Iterator<String> iterator = this.mBuddies.keySet().iterator();
            while (true) {
                if (iterator.hasNext()) {
                    String str = iterator.next();
                    Buddy buddy = this.mBuddies.get(str);
                    if (buddy.getLocation() != null) {
                        if (paramLong - buddy.getLocation().getTime() / 1000L < offlineThreshold) {
                            ((Buddy)this.mBuddies.get(str)).changeState(Buddy.UserState.ONLINE);
                            continue;
                        }
                        ((Buddy)this.mBuddies.get(str)).changeState(Buddy.UserState.OFFLINE);
                        continue;
                    }
                    ((Buddy)this.mBuddies.get(str)).changeState(Buddy.UserState.OFFLINE);
                    continue;
                }
                return;
            }
        }
    }
}
