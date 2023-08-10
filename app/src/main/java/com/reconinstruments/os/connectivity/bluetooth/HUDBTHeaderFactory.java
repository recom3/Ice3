package com.reconinstruments.os.connectivity.bluetooth;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDBTHeaderFactory {
    private static int a(byte[] paramArrayOfbyte, int paramInt) {
        return (paramArrayOfbyte[paramInt] & 0xFF) + ((paramArrayOfbyte[paramInt + 1] & 0xFF) << 8) + ((paramArrayOfbyte[paramInt + 2] & 0xFF) << 16) + ((paramArrayOfbyte[paramInt + 3] & 0xFF) << 24);
    }

    public static void a(byte[] paramArrayOfbyte, byte paramByte) {
        paramArrayOfbyte[1] = (byte)paramByte;
    }

    private static void a(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
        paramArrayOfbyte[paramInt1] = (byte)(byte)(paramInt2 & 0xFF);
        paramArrayOfbyte[paramInt1 + 1] = (byte)(byte)(paramInt2 >> 8 & 0xFF);
        paramArrayOfbyte[paramInt1 + 2] = (byte)(byte)(paramInt2 >> 16 & 0xFF);
        paramArrayOfbyte[paramInt1 + 3] = (byte)(byte)(paramInt2 >> 24 & 0xFF);
    }

    public static boolean isByte3equal1(byte[] paramArrayOfbyte) {
        boolean bool = true;
        if (paramArrayOfbyte[3] != 1)
            bool = false;
        return bool;
    }

    public static byte[] a() {
        byte[] arrayOfByte = buildHeader(false, (byte)3);
        arrayOfByte[2] = (byte)2;
        return arrayOfByte;
    }

    private static byte[] constructHeader(byte paramByte) {
        byte[] arrayOfByte = buildHeader(false, paramByte);
        arrayOfByte[4] = (byte)3;
        return arrayOfByte;
    }

    /**
     * This is the important one
     * @param paramInt1     payload
     * @param paramInt2     body
     * @return
     */
    public static byte[] adquireHeader(int paramInt1, int paramInt2) {
        byte[] arrayOfByte = buildHeader(true, (byte)1);
        arrayOfByte[4] = (byte)2;
        a(arrayOfByte, 23, paramInt1);
        if (paramInt2 > 0) {
            arrayOfByte[27] = (byte)1;
            a(arrayOfByte, 28, paramInt2);
            return arrayOfByte;
        }
        arrayOfByte[27] = (byte)0;
        return arrayOfByte;
    }

    public static byte[] adquireHeader2(int paramInt1, int paramInt2) {
        byte[] arrayOfByte = buildHeader(true, (byte)2);
        arrayOfByte[4] = (byte)2;
        a(arrayOfByte, 23, paramInt1);
        if (paramInt2 > 0) {
            arrayOfByte[27] = (byte)1;
            a(arrayOfByte, 28, paramInt2);
            return arrayOfByte;
        }
        arrayOfByte[27] = (byte)0;
        return arrayOfByte;
    }
    /**
     * This is used in ack?
     * @param paramBoolean
     * @return
     */
    public static byte[] constructHeader(boolean paramBoolean) {
        byte b = 2;
        byte[] arrayOfByte = constructHeader((byte)3);
        arrayOfByte[5] = (byte)2;
        if (paramBoolean)
            b = 1;
        arrayOfByte[6] = (byte)b;
        return arrayOfByte;
    }

    public static byte[] a(boolean paramBoolean, byte paramByte) {
        byte b = 2;
        byte[] arrayOfByte = constructHeader((byte)1);
        arrayOfByte[5] = (byte)2;
        if (paramBoolean)
            b = 1;
        arrayOfByte[6] = (byte)b;
        arrayOfByte[1] = (byte)paramByte;
        return arrayOfByte;
    }

    public static byte getByte1(byte[] paramArrayOfbyte) {
        return paramArrayOfbyte[1];
    }

    private static byte[] buildHeader(boolean paramBoolean, byte paramByte) {
        boolean bool = true;
        byte[] arrayOfByte = new byte[32];
        arrayOfByte[0] = (byte)1;
        arrayOfByte[2] = (byte)1;
        if (!paramBoolean)
            bool = false;
        arrayOfByte[22] = (byte)(bool ? 1 : 0);
        arrayOfByte[27] = (byte)0;
        arrayOfByte[3] = (byte)paramByte;
        arrayOfByte[16] = (byte)-113;
        arrayOfByte[17] = (byte)12;
        arrayOfByte[18] = (byte)65;
        arrayOfByte[19] = (byte)90;
        arrayOfByte[20] = (byte)107;
        arrayOfByte[21] = (byte)-101;
        return arrayOfByte;
    }

    //Probably set to 2 to call URL Adaptor
    //1?
    //3 to send the response?
    public static byte checkByte3(byte[] paramArrayOfbyte) {
        return paramArrayOfbyte[3];
    }

    /**
     * It is only used in HUDHttpBTConnection::sendHttpRequest
     * @param paramArrayOfbyte
     * @return
     */
    public static byte checkByte4(byte[] paramArrayOfbyte) {
        return paramArrayOfbyte[4];
    }

    public static byte e(byte[] paramArrayOfbyte) {
        return paramArrayOfbyte[5];
    }

    public static byte f(byte[] paramArrayOfbyte) {
        return paramArrayOfbyte[6];
    }

    public static boolean g(byte[] paramArrayOfbyte) {
        boolean bool = true;
        if (paramArrayOfbyte[22] != 1)
            bool = false;
        return bool;
    }

    public static int h(byte[] paramArrayOfbyte) {
        return a(paramArrayOfbyte, 23);
    }

    public static boolean i(byte[] paramArrayOfbyte) {
        boolean bool = true;
        if (paramArrayOfbyte[27] != 1)
            bool = false;
        return bool;
    }

    public static int j(byte[] paramArrayOfbyte) {
        return a(paramArrayOfbyte, 28);
    }

    public static boolean checkHeaderIsValid(byte[] paramArrayOfbyte) {
        boolean bool1 = false;
        if (paramArrayOfbyte.length < 32)
            return bool1;
        boolean bool2 = bool1;
        if (paramArrayOfbyte[16] == -113) {
            bool2 = bool1;
            if (paramArrayOfbyte[17] == 12) {
                bool2 = bool1;
                if (paramArrayOfbyte[18] == 65) {//A
                    bool2 = bool1;
                    if (paramArrayOfbyte[19] == 90) {//Z
                        bool2 = bool1;
                        if (paramArrayOfbyte[20] == 107) {//k
                            bool2 = bool1;
                            if (paramArrayOfbyte[21] == -101)
                                bool2 = true;
                        }
                    }
                }
            }
        }
        return bool2;
    }
}
