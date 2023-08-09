package com.recom3.connect.util;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
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

/**
 * Created by Recom3 on 27/01/2022.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static void base64Encode(byte[] paramArrayOfbyte) {}

    public static String gzipAndBase64Encode(byte[] paramArrayOfbyte) {
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
            paramArrayOfbyte = Base64.encode(arrayOfByte, 0);
            Log.d("FileUtils", "base64 length: " + paramArrayOfbyte.length);
            Log.d("FileUtils", "base64 string: " + new String(paramArrayOfbyte));
            String str = new String(paramArrayOfbyte);
            return str;
        } catch (IOException iOException) {
            Log.d("FileUtils", "error gzipping data stream", iOException);
            iOException = null;
            return null;
        }
        //return (String)iOException;
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
            FileInputStream fileInputStream = new FileInputStream(paramString);
            //this(paramString);
            byte[] arrayOfByte = readByteArray(fileInputStream, 0);
            if (paramBoolean)
                for (byte b = 24; b < 28; b++)
                    arrayOfByte[b] = (byte)0;
            String str = md5(arrayOfByte, 0);
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
            paramArrayOfbyte = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            //this();
            for (paramInt = 0; paramInt < paramArrayOfbyte.length; paramInt++) {
                stringBuffer.append(String.format("%02x", new Object[] { Integer.valueOf(paramArrayOfbyte[paramInt] & 0xFF) }));
            }
            str = stringBuffer.toString();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
            str = "";
        }
        return str;
    }

    public static byte[] readByteArray(File paramFile, int paramInt) {
        try {
            FileInputStream fileInputStream = new FileInputStream(paramFile);
            byte[] arrayOfByte = readByteArray(fileInputStream, paramInt);
            return arrayOfByte;
        } catch (FileNotFoundException fileNotFoundException) {
            Log.d("FileUtils", "failed to read byte array", fileNotFoundException);
            return null;
        }
    }

    //!!!!
    //Check all this function: several lines commented
    public static byte[] readByteArray(InputStream paramInputStream, int paramInt) {
        int i = paramInt;
        if (paramInt == 0) {
            try {
                i = paramInputStream.available();
                byte[] arrayOfByte4 = new byte[i];
                paramInputStream.read(arrayOfByte4);
                byte[] arrayOfByte3 = arrayOfByte4;
            } catch (IOException iOException) {
                Log.d("FileUtils", "failed to read byte array", iOException);
                //iOException = null;
                return null;
            }
            //return (byte[])iOException;
        }
        byte[] arrayOfByte2 = new byte[i];
        //iOException.read(arrayOfByte2);
        //byte[] arrayOfByte1 = arrayOfByte2;
        return arrayOfByte2;
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

        public String getFilePath(Context param1Context) {
            Context context = null;
            switch (this.type) {
                default:
                    //!!!! it was
                    //return (String)context
                    return (String)context.toString();
                case ROOT:
                    return this.path;
                case DATABASE:
                    return param1Context.getDatabasePath(this.path).getAbsolutePath();
                case STORAGE:
                    return Environment.getExternalStorageDirectory() + "/" + this.path;
                case TRIPS:
                    return Environment.getExternalStorageDirectory() + "/ReconApps/Tripdata/" + this.path;
                case FILES:
                    break;
            }
            return param1Context.getFilesDir().getAbsolutePath() + "/" + this.path;
        }

        public enum PathType {
            DATABASE, FILES, ROOT, STORAGE, TRIPS;

            static {
                //!!!!
                //$VALUES = new PathType[] { ROOT, DATABASE, STORAGE, TRIPS, FILES };
            }
        }
    }

    public enum PathType {
        DATABASE, FILES, ROOT, STORAGE, TRIPS;

        static {
            //!!!!
            //FILES = new PathType("FILES", 4);
            //$VALUES = new PathType[] { ROOT, DATABASE, STORAGE, TRIPS, FILES };
        }
    }
}
