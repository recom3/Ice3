package com.recom3.modlivemobile.utils;

public class TripData {
  public static int ALT;
  
  public static int BAR;
  
  public static int DIST;
  
  public static int HDOP;
  
  public static int LAT;
  
  public static int LONG;
  
  public static int SAT;
  
  public static int SPEED;
  
  public static int TEMP;
  
  public static int TIME = 0;
  
  public static int VERT;
  
  public static int fieldCount;
  
  public int[][] data;
  
  public int fileSize;
  
  public int lineCount;
  
  public long startTime;
  
  static {
    LAT = 1;
    LONG = 2;
    SPEED = 3;
    ALT = 4;
    BAR = 5;
    TEMP = 6;
    SAT = 7;
    HDOP = 8;
    VERT = 9;
    DIST = 10;
    fieldCount = 11;
  }
  
  public float getAltitude(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][ALT]);
  }
  
  public float getBarometer(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][BAR]);
  }
  
  public float getDistance(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][DIST]);
  }
  
  public float getHDOP(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][HDOP]);
  }
  
  public int getLatitude(int paramInt) {
    return this.data[paramInt][LAT];
  }
  
  public int getLongitude(int paramInt) {
    return this.data[paramInt][LONG];
  }
  
  public int getSatellites(int paramInt) {
    return this.data[paramInt][SAT];
  }
  
  public float getSpeed(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][SPEED]);
  }
  
  public float getTemperature(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][TEMP]);
  }
  
  public int getTime(int paramInt) {
    return this.data[paramInt][TIME];
  }
  
  public float getValue(int paramInt1, int paramInt2) {
    return (paramInt1 == TIME || paramInt1 == LAT || paramInt1 == LONG || paramInt1 == SAT) ? this.data[paramInt2][paramInt1] : Float.intBitsToFloat(this.data[paramInt2][paramInt1]);
  }
  
  public float getVertical(int paramInt) {
    return Float.intBitsToFloat(this.data[paramInt][VERT]);
  }
  
  public void putAltitude(int paramInt, float paramFloat) {
    this.data[paramInt][ALT] = Float.floatToRawIntBits(paramFloat);
  }
  
  public void putDistance(int paramInt, float paramFloat) {
    this.data[paramInt][DIST] = Float.floatToRawIntBits(paramFloat);
  }
  
  public void putVertical(int paramInt, float paramFloat) {
    this.data[paramInt][VERT] = Float.floatToRawIntBits(paramFloat);
  }
  
  public enum TripField {
    ALT, BAR, DIST, HDOP, LAT, LONG, SAT, SPEED, TEMP, TIME, VERT;

    //!!!
    /*
    static {
      ALT = new TripField("ALT", 4);
      BAR = new TripField("BAR", 5);
      TEMP = new TripField("TEMP", 6);
      SAT = new TripField("SAT", 7);
      HDOP = new TripField("HDOP", 8);
      VERT = new TripField("VERT", 9);
      DIST = new TripField("DIST", 10);
      ENUM$VALUES = new TripField[] {
          TIME, LAT, LONG, SPEED, ALT, BAR, TEMP, SAT, HDOP, VERT,
          DIST };
    }
    */
  }
}


/* Location:              D:\Downloads\Recon-Oakley\apk\dex-tools-2.1\ConnectDevice.jar!\com\reconinstruments\modlivemobil\\utils\TripData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */