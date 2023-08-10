package com.recom3.mobilesdk.buddytracking;

import android.location.Location;

import com.recom3.connect.messages.BuddyInfoMessage;
import com.recom3.snow3.mobilesdk.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Recom3 on 06/07/2022.
 */

public class Buddy {
    private String bio;

    private BuddyState buddyState = null;

    private String city;

    private int color = 0;

    private String countryId;

    private String email;

    private String gender;

    private String id;

    private Location location;

    private String name;

    private String pictureLink;

    private UserState state;

    public Buddy() {}

    public Buddy(JSONObject paramJSONObject) throws JSONException, IllegalArgumentException {
        this.id = paramJSONObject.getString("id");
        String str1 = "";
        String str2 = "";
        if (paramJSONObject.has("first_name"))
            str1 = paramJSONObject.getString("first_name");
        if (paramJSONObject.has("last_name"))
            str2 = " " + paramJSONObject.getString("last_name");
        this.name = str1 + str2;
        JSONObject jSONObject1 = paramJSONObject.optJSONObject("UserProfile");
        if (jSONObject1 != null) {
            if (jSONObject1.has("picture"))
                this.pictureLink = jSONObject1.getString("picture");
            if (jSONObject1.has("city"))
                this.city = jSONObject1.getString("city");
            if (jSONObject1.has("country_id"))
                this.countryId = jSONObject1.getString("country_id");
            if (jSONObject1.has("gender"))
                this.gender = jSONObject1.getString("gender");
            if (jSONObject1.has("bio"))
                this.bio = jSONObject1.getString("bio");
        }
        JSONObject jSONObject2 = paramJSONObject.optJSONObject("UserLocation");
        if (jSONObject2 != null) {
            String str3;
            String str4 = null;
            long l;
            String str5 = jSONObject2.getString("latitude");
            str2 = jSONObject2.getString("longitude");
            if (!jSONObject2.isNull("provider")) {
                str3 = jSONObject2.getString("provider");
            } else {
                str3 = "unknown";
            }
            jSONObject1 = null;
            if (!jSONObject2.isNull("accuracy"))
                str4 = jSONObject2.optString("accuracy");
            if (!jSONObject2.isNull("location_time")) {
                l = Long.parseLong(jSONObject2.optString("location_time")) * 1000L;
            } else {
                l = DateUtils.jsonStringToDate(jSONObject2.optString("modified"));
            }
            this.location = new Location(str3);
            this.location.setLatitude(Double.valueOf(str5).doubleValue());
            this.location.setLongitude(Double.valueOf(str2).doubleValue());
            if (str4 != null)
                this.location.setAccuracy(Float.valueOf(str4).floatValue());
            this.location.setTime(l);
        }
    }

    public Buddy(JSONObject paramJSONObject, boolean paramBoolean) throws JSONException, IllegalArgumentException {
        this.id = paramJSONObject.getString("id");
        this.name = paramJSONObject.getString("first_name") + " " + paramJSONObject.getString("last_name");
        if (false) {
            this.state = UserState.ONLINE;
            return;
        }
        this.state = UserState.OFFLINE;
    }

    void changeState(UserState paramUserState) {
        this.state = paramUserState;
    }

    public BuddyInfoMessage.BuddyInfo getBuddyInfo() {
        return new BuddyInfoMessage.BuddyInfo(Integer.parseInt(this.id), this.name, this.location.getLatitude(), this.location.getLongitude(), this.location.getTime());
    }

    public BuddyState getBuddyState() {
        return this.buddyState;
    }

    public int getColor() {
        return this.color;
    }

    public String getEmail() {
        return this.email;
    }

    public String getID() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public UserState getState() {
        return this.state;
    }

    public long getUpdateTime() {
        return (this.location != null) ? this.location.getTime() : 0L;
    }

    public boolean isOnline() {
        return (getState() == null) ? false : getState().equals(UserState.ONLINE);
    }

    public void merge(Buddy paramBuddy) {
        if (paramBuddy.buddyState != null)
            setBuddyState(paramBuddy.buddyState);
        if (paramBuddy.email != null)
            this.email = paramBuddy.email;
        if (paramBuddy.name != null)
            this.name = paramBuddy.name;
        if (paramBuddy.state != null)
            changeState(paramBuddy.state);
        if (paramBuddy.location != null)
            setLocation(paramBuddy.location);
    }

    void setBuddyState(BuddyState paramBuddyState) {
        this.buddyState = paramBuddyState;
    }

    public void setBuddyStateAsRequesting() {
        setBuddyState(BuddyState.REQUESTING);
    }

    void setEmail(String paramString) {
        this.email = paramString;
    }

    void setLocation(Location paramLocation) {
        this.location = paramLocation;
    }

    void setState(UserState paramUserState) {
        this.state = paramUserState;
    }

    public enum BuddyState {
        ACCEPTED, REJECTED, REMOVED, REQUESTED, REQUESTING, SELF;

        static {
            //!!!
            //ACCEPTED = new BuddyState("ACCEPTED", 3);
            //REJECTED = new BuddyState("REJECTED", 4);
            //REMOVED = new BuddyState("REMOVED", 5);
            //$VALUES = new BuddyState[] { SELF, REQUESTED, REQUESTING, ACCEPTED, REJECTED, REMOVED };
        }

        public String toAction() {
            switch (this) {
                default:
                    return "";
                case ACCEPTED:
                    return "accept";
                case REJECTED:
                    return "reject";
                case REMOVED:
                    break;
            }
            return "remove";
        }
    }

    public enum UserState {
        OFFLINE, ONLINE;

        static {

        }
    }
}
