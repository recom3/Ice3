package com.recom3.modlivemobile.utils;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

class Memory {
  public static final int sizeOf(Object paramObject) {
    int i = -1;
    if (paramObject != null)
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //this();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        //this(byteArrayOutputStream);
        objectOutputStream.writeObject(paramObject);
        objectOutputStream.flush();
        objectOutputStream.close();
        byte[] tyteArr = byteArrayOutputStream.toByteArray();
        if (tyteArr == null)
          return 0; 
        int j = tyteArr.length;
        i = j;
      } catch (Exception exception) {
        Log.d("Memory", "error getting sizeOf", exception);
      }  
    return i;
  }
}


/* Location:              D:\Downloads\Recon-Oakley\apk\dex-tools-2.1\ConnectDevice.jar!\com\reconinstruments\modlivemobil\\utils\Memory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */