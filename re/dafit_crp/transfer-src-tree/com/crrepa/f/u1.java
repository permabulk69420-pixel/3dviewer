/*
 * Decompiled with CFR 0.152.
 */
package com.crrepa.f;

import com.crrepa.l.a;

public class u1 {
    private static final int a = 255;

    private u1() {
    }

    public static byte[] a(int n, byte[] byArray) {
        int n2 = 0;
        if (byArray != null) {
            n2 = byArray.length;
        }
        int n3 = n2 + 5;
        byte[] byArray2 = new byte[n3];
        byte[] byArray3 = byArray2;
        byArray3[0] = -2;
        byArray3[1] = -22;
        byArray2[3] = (byte)n3;
        byArray3[2] = u1.a() == 20 ? 16 : (255 < n3 ? (int)((n3 >> 8) + 32) : 32);
        byArray3[4] = (byte)n;
        if (n2 > 0) {
            n = byArray.length;
            System.arraycopy(byArray, 0, byArray3, 5, n);
        }
        return byArray3;
    }

    public static int a() {
        return com.crrepa.l.a.b().c();
    }
}
