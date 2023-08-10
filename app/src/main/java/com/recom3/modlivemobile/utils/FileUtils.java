package com.recom3.modlivemobile.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

public class FileUtils {
  public static final int SQLITEHEADEROFFSET = 28;
  
  public static String gzipAndBase64Encode(byte[] paramArrayOfbyte) {
    String str = null;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
      //this(byteArrayOutputStream);
      gZIPOutputStream.write(paramArrayOfbyte);
      gZIPOutputStream.finish();
      byteArrayOutputStream.close();
      byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
      gZIPOutputStream.close();
      Log.d("FileUtils", "binary length: " + paramArrayOfbyte.length);
      Log.d("FileUtils", "gzipped length: " + arrayOfByte.length);
      paramArrayOfbyte = Base64.encodeBase64(arrayOfByte);
      Log.d("FileUtils", "base64 length: " + paramArrayOfbyte.length);
      Log.d("FileUtils", "base64 string: " + new String(paramArrayOfbyte));
      str = new String(paramArrayOfbyte);
    } catch (IOException iOException) {
      Log.d("FileUtils", "error gzipping data stream", iOException);
      iOException = null;
    } 
    //return (String)iOException;
    return str;
  }
  
  public static boolean hasStorage(boolean paramBoolean) {
    boolean bool = true;
    String str = Environment.getExternalStorageState();
    if ("mounted".equals(str))
      return bool; 
    if (!paramBoolean) {
      paramBoolean = bool;
      return !"mounted_ro".equals(str) ? false : paramBoolean;
    } 
    return false;
  }

  public static String md5(String paramString, boolean paramBoolean) {
    try {
      String str = null;
      FileInputStream fileInputStream = new FileInputStream(paramString);
      //this(paramString);
      byte[] arrayOfByte = readByteArray(fileInputStream, 0);
      if (paramBoolean) {
        byte b = 23;
        while (true) {
          if (b < 28) {
            arrayOfByte[b] = (byte)0;
            b++;
            continue;
          } 
          str = md5(arrayOfByte, 0);
          paramString = str;
        } 
      }
      //byte[] b = string.getBytes();
      //byte[] b = string.getBytes(Charset.forName("UTF-8"));
      //byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
      //str = md5((byte[])str, 0);
      str = md5(str.getBytes(), 0);
      paramString = str;
    } catch (FileNotFoundException fileNotFoundException) {
      Log.d("FileUtils", "Tried to get checksum on missing file: " + paramString);
      paramString = "";
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      Log.d("FileUtils", "Tried to get checksum on empty file: " + paramString, arrayIndexOutOfBoundsException);
      paramString = "";
    } 
    return paramString;
  }

  public static String md5(byte[] paramArrayOfbyte, int paramInt) {
    String str;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.update(paramArrayOfbyte, paramInt, paramArrayOfbyte.length - paramInt);
      byte[] arrayOfByte = messageDigest.digest();
      StringBuffer stringBuffer = new StringBuffer();
      //this();
      for (paramInt = 0;; paramInt++) {
        if (paramInt >= arrayOfByte.length)
          return stringBuffer.toString(); 
        stringBuffer.append(Integer.toHexString(arrayOfByte[paramInt] & 0xFF));
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      noSuchAlgorithmException.printStackTrace();
      str = "";
    } 
    return str;
  }
  
  public static byte[] readByteArray(File paramFile, int paramInt) {
    byte[] arrayOfByte = null;
    try {
      FileInputStream fileInputStream = new FileInputStream(paramFile);
      //this(paramFile);
      arrayOfByte = readByteArray(fileInputStream, paramInt);
    } catch (FileNotFoundException fileNotFoundException) {
      Log.d("FileUtils", "failed to read byte array", fileNotFoundException);
      fileNotFoundException = null;
    } 
    return arrayOfByte;
  }
  
  public static byte[] readByteArray(InputStream paramInputStream, int paramInt) {
    byte[] arrayOfByte3 = null;
    int i = paramInt;
    if (paramInt == 0) {
      try {
        i = paramInputStream.available();
        byte[] arrayOfByte4 = new byte[i];
        paramInputStream.read(arrayOfByte4);
        arrayOfByte3 = arrayOfByte4;
      } catch (IOException iOException) {
        Log.d("FileUtils", "failed to read byte array", iOException);
        iOException = null;
      } 
      //return (byte[])iOException;
      return arrayOfByte3;
    } 
    byte[] arrayOfByte2 = new byte[i];
    //iOException.read(arrayOfByte2);
    try {
      paramInputStream.read(arrayOfByte2, 0, i);
      arrayOfByte3 = arrayOfByte2;
    } catch (IOException e) {
      e.printStackTrace();
    }
    //byte[] arrayOfByte1 = arrayOfByte2;
    return arrayOfByte3;
  }
  
  public static class FilePath implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String path;
    
    public PathType type;
    
    public FilePath(String param1String, PathType param1PathType) {
      this.path = param1String;
      this.type = param1PathType;
    }
    
    public boolean equals(Object param1Object) {
      return (param1Object instanceof FilePath) ? ((((FilePath)param1Object).type == this.type && ((FilePath)param1Object).path.equals(this.path))) : false;
    }
    
    public boolean fileEquals(FilePath param1FilePath) {
      return (new File(param1FilePath.path)).getName().equals((new File(this.path)).getName());
    }

    /*
    public String getFilePath(Context param1Context) {
      Context context = null;
      switch (this.type) {
        default:
          return (String)context;
        case null:
          return this.path;
        case null:
          return param1Context.getDatabasePath(this.path).getAbsolutePath();
        case null:
          return Environment.getExternalStorageDirectory() + "/" + this.path;
        case null:
          return Environment.getExternalStorageDirectory() + "/ReconApps/Tripdata/" + this.path;
        case null:
          break;
      } 
      return String.valueOf(param1Context.getFilesDir().getAbsolutePath()) + "/" + this.path;
    }
    */

    public enum PathType {
      DATABASE, FILES, ROOT, STORAGE, TRIPS;

      //!!!
      /*
      static {
        ENUM$VALUES = new PathType[] { ROOT, DATABASE, STORAGE, TRIPS, FILES };
      }
      */
    }
  }
  
  public enum PathType {
    DATABASE, FILES, ROOT, STORAGE, TRIPS;

    //!!!
    /*
    static {
      FILES = new PathType("FILES", 4);
      ENUM$VALUES = new PathType[] { ROOT, DATABASE, STORAGE, TRIPS, FILES };
    }
    */
  }
}


/* Location:              D:\Downloads\Recon-Oakley\apk\dex-tools-2.1\ConnectDevice.jar!\com\reconinstruments\modlivemobil\\utils\FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */