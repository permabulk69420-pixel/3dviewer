/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 */
package com.crrepa.p0;

import android.text.TextUtils;
import java.nio.charset.Charset;

public class d {
    private static final String a = "0123456789abcdef";

    public static String c(byte[] byArray) {
        StringBuilder stringBuilder;
        if (byArray == null) {
            return "null";
        }
        StringBuilder stringBuilder2 = stringBuilder;
        stringBuilder = new StringBuilder();
        for (int i = 0; i < byArray.length; ++i) {
            String string = Integer.toHexString(byArray[i] & 0xFF);
            if (string.length() == 1) {
                string = "0" + string;
            }
            StringBuilder stringBuilder3 = stringBuilder2;
            stringBuilder3.append(string);
            stringBuilder3.append(" ");
        }
        return stringBuilder2.toString();
    }

    public static int a(byte by) {
        return by & 0xFF;
    }

    public static int b(byte by, byte by2) {
        return ((by & 0xFF) << 8) + (by2 & 0xFF);
    }

    public static int a(byte by, byte by2) {
        return by << 8 | by2 & 0xFF;
    }

    public static byte[] b(int n) {
        return new byte[]{(byte)(n >> 8 & 0xFF), (byte)(n & 0xFF)};
    }

    public static byte[] a(int n) {
        byte[] byArray = new byte[2];
        byArray[1] = (byte)(n >> 8 & 0xFF);
        byArray[0] = (byte)(n & 0xFF);
        return byArray;
    }

    public static byte[] c(long l) {
        byte[] byArray = new byte[4];
        byte[] byArray2 = byArray;
        byArray2[0] = (byte)(l >> 24 & 0xFFL);
        byArray2[1] = (byte)(l >> 16 & 0xFFL);
        byArray2[2] = (byte)(l >> 8 & 0xFFL);
        byArray[3] = (byte)(l & 0xFFL);
        return byArray;
    }

    public static byte[] b(long l) {
        byte[] byArray = new byte[4];
        byte[] byArray2 = byArray;
        byArray2[3] = (byte)(l >> 24 & 0xFFL);
        byArray2[2] = (byte)(l >> 16 & 0xFFL);
        byArray2[1] = (byte)(l >> 8 & 0xFFL);
        byArray[0] = (byte)(l & 0xFFL);
        return byArray;
    }

    public static byte[] d(long l) {
        byte[] byArray = new byte[4];
        byte[] byArray2 = byArray;
        byArray2[3] = (byte)(l >> 24);
        byArray2[2] = (byte)(l >> 16);
        byArray2[1] = (byte)(l >> 8);
        byArray[0] = (byte)l;
        return byArray;
    }

    public static byte[] a(long l) {
        byte[] byArray = new byte[8];
        byte[] byArray2 = byArray;
        byArray2[7] = (byte)(l >> 56 & 0xFFL);
        byArray2[6] = (byte)(l >> 48 & 0xFFL);
        byArray2[5] = (byte)(l >> 40 & 0xFFL);
        byArray2[4] = (byte)(l >> 32 & 0xFFL);
        byArray2[3] = (byte)(l >> 24 & 0xFFL);
        byArray2[2] = (byte)(l >> 16 & 0xFFL);
        byArray2[1] = (byte)(l >> 8 & 0xFFL);
        byArray[0] = (byte)(l & 0xFFL);
        return byArray;
    }

    public static long e(byte[] byArray) {
        return ((long)(byArray[3] & 0xFF) << 24) + (long)((byArray[2] & 0xFF) << 16) + (long)((byArray[1] & 0xFF) << 8) + (long)(byArray[0] & 0xFF);
    }

    public static int h(byte[] byArray) {
        byte by = byArray[3];
        int n = (by << 25 >> 1) + ((byArray[2] & 0xFF) << 16) + ((byArray[1] & 0xFF) << 8) + (byArray[0] & 0xFF);
        if (by < 0) {
            return -n;
        }
        return n;
    }

    public static int d(byte[] byArray) {
        return ((byArray[0] & 0xFF) << 24) + ((byArray[1] & 0xFF) << 16) + ((byArray[2] & 0xFF) << 8) + (byArray[3] & 0xFF);
    }

    public static byte[] a(String object) {
        String string = object;
        object = new byte[6];
        String[] stringArray = string.split(":");
        for (int i = 0; i < stringArray.length; ++i) {
            object[i] = (byte)Integer.parseInt(stringArray[i], 16);
        }
        return object;
    }

    public static int i(byte[] byArray) {
        if (!d.g(byArray)) {
            return 0;
        }
        return byArray[0] & 0xFF | byArray[1] << 8 & 0xFF00 | byArray[2] << 24 >>> 8;
    }

    private static boolean g(byte[] byArray) {
        int n = byArray.length;
        for (int i = 0; i < n; ++i) {
            if (byArray[i] == -1) continue;
            return true;
        }
        return false;
    }

    public static int a(byte[] byArray) {
        return byArray[2] & 0xFF | byArray[1] << 8 & 0xFF00 | byArray[0] << 24 >>> 8;
    }

    public static byte a(char c) {
        return (byte)a.indexOf(c);
    }

    public static String j(byte[] byArray) {
        StringBuilder stringBuilder;
        StringBuilder stringBuilder2 = stringBuilder;
        stringBuilder = new StringBuilder();
        for (int i = 0; i < byArray.length; ++i) {
            stringBuilder2.append(a.charAt(byArray[i] % 16));
        }
        return stringBuilder2.toString();
    }

    public static int b(String string) {
        int n = string.length();
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (d.a(string.charAt(i)) <= 0) continue;
            double d2 = n - i - 1;
            n2 = (int)((double)n2 + Math.pow(2.0, d2));
        }
        return n2;
    }

    public static boolean f(byte[] byArray) {
        return byArray == null || byArray.length == 0;
    }

    public static byte[] a(String string, int n, Charset charset) {
        if (TextUtils.isEmpty((CharSequence)string)) {
            return null;
        }
        if (n < string.length()) {
            string = string.substring(0, n);
        }
        while (n < string.getBytes(charset).length) {
            String string2 = string;
            string = string2.substring(0, string2.length() - 1);
        }
        return string.getBytes(charset);
    }

    public static int a(byte[] byArray, byte[] byArray2) {
        if (byArray != null && byArray2 != null && byArray.length != 0 && byArray2.length != 0) {
            for (int i = 0; i < byArray.length; ++i) {
                int n;
                if (byArray[i] != byArray2[0] || i + byArray2.length >= byArray.length) continue;
                for (n = 1; n < byArray2.length && byArray[i + n] == byArray2[n]; ++n) {
                }
                if (n != byArray2.length) continue;
                return i;
            }
            return -1;
        }
        return -1;
    }

    public static String b(byte[] byArray) {
        StringBuilder stringBuilder;
        if (byArray == null) {
            return "null";
        }
        StringBuilder stringBuilder2 = stringBuilder;
        stringBuilder = new StringBuilder();
        for (int i = 0; i < byArray.length; ++i) {
            String string = Integer.toHexString(byArray[i] & 0xFF);
            if (string.length() == 1) {
                string = "0" + string;
            }
            stringBuilder2.append(string);
        }
        return stringBuilder2.toString();
    }
}
