package com.recom3.modlivemobile.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.recom3.connect.messages.TransferResponseMessage;
import com.recom3.modlivemobile.utils.TripData;
//import com.reconinstruments.modlivemobile.dto.message.TransferResponseMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class FileController {
  private static final String TAG = "FileController";
  
  static Calendar UTCcal;
  
  public static final String appStorage = "ReconApps";
  
  public static final String appStoragePath;
  
  static boolean firstTime = false;
  
  private static final short[] mAltCoefficient_i;
  
  private static final short[] mAltCoefficient_j;
  
  private static final short[] mPressure_Delimiter;
  
  public static String parseDataInfo = "";
  
  public static final String sdPath = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/";
  
  static long startTime = 0L;
  
  public static final String tripData = "TripData";
  
  public static final String tripPath;
  
  static {
    appStoragePath = String.valueOf(sdPath) + "ReconApps" + "/";
    tripPath = String.valueOf(appStoragePath) + "TripData" + "/";
    firstTime = true;
    UTCcal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.getDefault());
    mPressure_Delimiter = new short[] { 
        1000, 1130, 1300, 1500, 1730, 2000, 2300, 2650, 3000, 3350, 
        3700, 4100, 4500, 5000, 5500, 6000, 6500, 7100, 7800, 8500, 
        9200, 9700, 10300, 11000 };
    mAltCoefficient_i = new short[] { 
        12256, 10758, 9329, 8085, 7001, 6069, 5360, 4816, 4371, 4020, 
        3702, 3420, 3158, 2908, 2699, 2523, 2359, 2188, 2033, 1905, 
        1802, 1720, 1638 };
    mAltCoefficient_j = new short[] { 
        16212, 15434, 14541, 13630, 12722, 11799, 10910, 9994, 9171, 8424, 
        7737, 7014, 6346, 5575, 4865, 4206, 3590, 2899, 2151, 1456, 
        805, 365, -139 };
  }
  
  public static float getBarometerAlt(int paramInt) {
    if (paramInt <= 0)
      return 10000.0F; 
    int i = paramInt * 100;
    paramInt = 22;
    while (true) {
      if (paramInt > 0 && i < mPressure_Delimiter[paramInt] * 10) {
        paramInt = (char)(paramInt - 1);
        continue;
      } 
      return ((mAltCoefficient_j[paramInt] * 10 - ((i - mPressure_Delimiter[paramInt] * 10) * mAltCoefficient_i[paramInt] >> 11)) / 10);
    } 
  }
  
  public static File[] getTripList(Context paramContext) {
    File file = new File(tripPath);
    file.mkdirs();
    return file.listFiles();
  }

  public static TransferResponseMessage.FileInfo[] getTripListing() {
    File[] listFiles = new File(tripPath).listFiles();
    ArrayList arrayList = new ArrayList();
    for (int i = 0; i < listFiles.length; i++) {
      String[] split = listFiles[i].getName().split("[.]");
      if (split[split.length - 1].toLowerCase().equalsIgnoreCase("rib")) {
        arrayList.add(Integer.valueOf(i));
      }
    }
    ArrayList arrayList2 = new ArrayList();
    for (int i2 = 0; i2 < arrayList.size(); i2++) {
      try {
        TripData parseTripFile = parseTripFile( listFiles[((Integer) arrayList.get(i2)).intValue()], 2 );
        if (parseTripFile != null) {
          arrayList2.add(new TransferResponseMessage.FileInfo(listFiles[((Integer) arrayList.get(i2)).intValue()].getName(), parseTripFile.startTime, parseTripFile.lineCount));
        }
      } catch (Exception e) {
      }
    }
    TransferResponseMessage.FileInfo[] fileInfoArr = new TransferResponseMessage.FileInfo[arrayList2.size()];
    arrayList2.toArray(fileInfoArr);
    return fileInfoArr;
  }

  public static TripData parseTripFile(File paramFile, int paramInt) {
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(paramFile);
    } catch (FileNotFoundException e) {
      Log.e(TAG, e.getMessage());
    }
    return parseTripFile(fileInputStream, paramInt);
  }

  //public static TripData parseTripFile(File paramFile, int paramInt) {
  public static TripData parseTripFile(FileInputStream fileInputStream, int paramInt) {
    try {
      //FileInputStream fileInputStream = new FileInputStream(paramFile);
      //this(paramFile);
      int i = fileInputStream.available();
      int j = i;
      if (paramInt != 0)
        j = paramInt * 30; 
      return parseTripFile(FileUtils.readByteArray(fileInputStream, j), paramInt, i);
    } catch (FileNotFoundException fileNotFoundException) {
      Log.d("FileController", "failed to parse Trip File", fileNotFoundException);
    } catch (IOException iOException) {
      Log.d("FileController", "failed to parse RIB File", iOException);
    } 
    return null;
  }
  
  public static TripData parseTripFile(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    TripData tripData = null;

    if (paramArrayOfbyte[2] == 17)
      return null; 
    byte b = paramArrayOfbyte[0];
    int i = b & 0x3F;
    try {
      StringBuilder stringBuilder = new StringBuilder("Version: ");
      //this("Version: ");
      Log.v("FileController", stringBuilder.append((b & 0xC0) >> 6).toString());
      stringBuilder = new StringBuilder("Field Count: ");
      //this("Field Count: ");
      Log.v("FileController", stringBuilder.append(i).toString());
      if (i == 0)
        return null; 
      byte[] arrayOfByte = new byte[i];
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      int j = readBytes(paramArrayOfbyte, arrayOfByte, 0 + 1, i);
      b = 0;
      int k = 0;
      int m=0;
      while (true) {
        //int m=0;
        if (k >= i) {
          if (b == 0) {
            paramArrayOfbyte = null;
            // Byte code: goto -> 10
          } 
        } else {
          arrayOfInt1[k] = arrayOfByte[k] & 0x3F;
          arrayOfInt2[k] = ((arrayOfByte[k] & 0xC0) >> 6) + 1;
          StringBuilder stringBuilder2 = new StringBuilder("Field[");
          //this("Field[");
          Log.v("FileController", stringBuilder2.append(k).append("]: id:").append(arrayOfInt1[k]).append(", size: ").append(arrayOfInt2[k]).toString());
          m = b + arrayOfInt2[k];
          k++;
          continue;
        } 
        int[] arrayOfInt = new int[m];
        k = paramInt2 - j + 1;
        int n = k / m;
        if (n == 0) {
          Log.d("FileController", "File has no trip data");
          paramArrayOfbyte = null;
          // Byte code: goto -> 10
        } 
        StringBuilder stringBuilder1 = new StringBuilder("fieldCount  : ");
        //this("fieldCount  : ");
        parseDataInfo = stringBuilder1.append(i).append("\n").append("lineDataSize    : ").append(k).append("\n").append("lineByteSize: ").append(m).append("\n").append("line count  : ").append(n).toString();
        Log.v("FileController", parseDataInfo);
        k = paramInt1;
        if (paramInt1 == 0)
          k = n; 
        paramInt1 = TripData.fieldCount;
        int[][] arrayOfInt3 = new int[k][paramInt1];
        startTime = 0L;
        firstTime = true;
        byte b1 = 0;
        paramInt1 = 0;
        label43: while (true) {
          //TripData tripData = null;
          StringBuilder stringBuilder2 = null;
          if (paramInt1 >= k) {
            tripData = new TripData();
            //this();
            tripData.startTime = startTime;
            tripData.fileSize = paramInt2;
            tripData.lineCount = n;
            tripData.data = arrayOfInt3;
            stringBuilder2 = new StringBuilder("ribData size: ");
            //this("ribData size: ");
            Log.d("FileController", stringBuilder2.append(Memory.sizeOf(arrayOfInt3[1]) * n).toString());
            return tripData;
          }

          byte[] yourBytes = null;
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          ObjectOutputStream out = null;
          try {
            out = new ObjectOutputStream(bos);
            out.writeObject(tripData);
            out.flush();
            yourBytes = bos.toByteArray();
          } finally {
            try {
              bos.close();
            } catch (IOException ex) {
              // ignore close exception
            }
          }

          //int i1 = readInts((byte[])tripData, arrayOfInt, j, m);
          int i1 = readInts(yourBytes, arrayOfInt, j, m);

          arrayOfInt3[b1] = new int[TripData.fieldCount];
          if (paramInt1 != 0)
            arrayOfInt3[b1][0] = paramInt1 - 1; 
          int i2 = 0;
          for (j = 0;; j++) {
            if (j >= i) {
              b1++;
              paramInt1++;
              j = i1;
              continue label43;
            } 
            //readDataField(arrayOfInt3[b1], stringBuilder2[j], arrayOfInt, i2);
            readDataField(arrayOfInt3[b1], Integer.parseInt(""+stringBuilder2.charAt(j)), arrayOfInt, i2);
            int i3 = arrayOfInt2[j];
            i2 += i3;
          }
          //!!!
          //break;
        }
        //!!!
        //break;
      } 
    } catch (Exception exception) {
      Log.d("FileController", "failed to parse RIB run", exception);
      exception = null;
    } 
    //return (TripData)exception;
    return tripData;
  }
  
  private static int readBytes(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
    for (byte b = 0;; b++) {
      if (b >= paramInt2)
        return paramInt1 + paramInt2; 
      paramArrayOfbyte2[b] = (byte)paramArrayOfbyte1[b + paramInt1];
    } 
  }
  
  private static void readDataField(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2) {
    int i;
    float f;
    switch (paramInt1) {
      default:
        return;
      case 0:
        if ((paramArrayOfint2[paramInt2] & 0x80) == 0)
          if (firstTime) {
            paramInt1 = paramArrayOfint2[paramInt2 + 0];
            int j = paramArrayOfint2[paramInt2 + 1];
            paramInt2 = paramArrayOfint2[paramInt2 + 2];
            UTCcal.set(Calendar.HOUR_OF_DAY, paramInt1);
            UTCcal.set(Calendar.MINUTE, j);
            UTCcal.set(Calendar.SECOND, paramInt2);
            UTCcal.set(Calendar.MILLISECOND, 0);
            startTime = UTCcal.getTimeInMillis();
            firstTime = false;
          }  
        paramInt1 = paramArrayOfint2[paramInt2 + 0];
        i = paramArrayOfint2[paramInt2 + 1];
        paramInt2 = paramArrayOfint2[paramInt2 + 2];
        UTCcal.set((paramInt1 & 0x7F) + 2000, i - 1, paramInt2);
        firstTime = true;
      case 1:
        i = (int)((paramArrayOfint2[paramInt2 + 0] + ((paramArrayOfint2[paramInt2 + 1] & 0x7F) + paramArrayOfint2[paramInt2 + 2] * 0.01D + paramArrayOfint2[paramInt2 + 3] * 1.0E-4D) / 60.0D) * 1000000.0D);
        paramInt1 = i;
        if ((paramArrayOfint2[paramInt2 + 1] & 0x80) != 0)
          paramInt1 = -i; 
        paramArrayOfint1[TripData.LAT] = paramInt1;
      case 2:
        i = (int)((paramArrayOfint2[paramInt2 + 0] + ((paramArrayOfint2[paramInt2 + 1] & 0x7F) + paramArrayOfint2[paramInt2 + 2] * 0.01D + paramArrayOfint2[paramInt2 + 3] * 1.0E-4D) / 60.0D) * 1000000.0D);
        paramInt1 = i;
        if ((paramArrayOfint2[paramInt2 + 1] & 0x80) != 0)
          paramInt1 = -i; 
        paramArrayOfint1[TripData.LONG] = paramInt1;
      case 3:
        f = (paramArrayOfint2[paramInt2] * 256 + paramArrayOfint2[paramInt2 + 1]) / 10.0F;
        paramArrayOfint1[TripData.SPEED] = Float.floatToRawIntBits(f);
      case 4:
        f = (paramArrayOfint2[paramInt2] * 256 + paramArrayOfint2[paramInt2 + 1]);
        paramArrayOfint1[TripData.ALT] = Float.floatToRawIntBits(f);
      case 5:
        f = getBarometerAlt((paramArrayOfint2[paramInt2] * 256 + paramArrayOfint2[paramInt2 + 1]) / 50);
        paramArrayOfint1[TripData.BAR] = Float.floatToRawIntBits(f);
      case 6:
        paramInt1 = paramArrayOfint2[paramInt2];
        paramArrayOfint1[TripData.TEMP] = Float.floatToRawIntBits((paramInt1 - 40));
      case 7:
        break;
    } 
    paramInt1 = paramArrayOfint2[paramInt2];
    //float f = (((paramArrayOfint2[paramInt2] & 0x3) * 256 + paramArrayOfint2[paramInt2 + 1]) / 10);
    f = (((paramArrayOfint2[paramInt2] & 0x3) * 256 + paramArrayOfint2[paramInt2 + 1]) / 10);
    paramArrayOfint1[TripData.SAT] = (paramInt1 & 0x3C) >> 2;
    paramArrayOfint1[TripData.HDOP] = Float.floatToRawIntBits(f);
  }
  
  private static int readInts(byte[] paramArrayOfbyte, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    for (byte b = 0;; b++) {
      if (b >= paramInt2)
        return paramInt1 + paramInt2; 
      paramArrayOfint[b] = paramArrayOfbyte[b + paramInt1] & 0xFF;
    } 
  }
  
  public static void renameTrips(Context paramContext) {
    File[] arrayOfFile = (new File(tripPath)).listFiles();
    if (arrayOfFile != null) {
      int i = arrayOfFile.length;
      byte b = 0;
      while (true) {
        if (b < i) {
          File file = arrayOfFile[b];
          if (!file.getPath().contains("DAY") && !file.getPath().contains("EVT")) {
            String str = FileUtils.md5(file.getAbsolutePath(), false);
            file.renameTo(new File(file.getParentFile().getAbsoluteFile() + "/DAY[" + str + "].RIB"));
          } 
          b++;
          continue;
        } 
        return;
      } 
    } 
  }
  
  public static void saveFileToExternalStorage(String paramString, byte[] paramArrayOfbyte) throws FileNotFoundException {
    File file = new File(paramString);
    if (Build.VERSION.SDK_INT > 9 && !file.setWritable(true, false))
      Log.d("FileController", "Failed to make file writable"); 
    try {
      file.getParentFile().mkdirs();
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      //this(file);
      fileOutputStream.write(paramArrayOfbyte);
      fileOutputStream.close();
      StringBuilder stringBuilder = new StringBuilder("Saved file: ");
      //this("Saved file: ");
      Log.d("FileController", stringBuilder.append(file.getAbsolutePath()).toString());
    } catch (FileNotFoundException fileNotFoundException) {
      throw fileNotFoundException;
    } catch (IOException iOException) {
      Log.e("FileController", "Failed to save trip.", iOException);
    } 
  }
}


/* Location:              D:\Downloads\Recon-Oakley\apk\dex-tools-2.1\ConnectDevice.jar!\com\reconinstruments\modlivemobil\\utils\FileController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */