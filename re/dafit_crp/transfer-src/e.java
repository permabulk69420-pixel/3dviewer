/*
 * Decompiled with CFR 0.152.
 */
package com.crrepa.c0;

import com.crrepa.c0.b;
import com.crrepa.l.a;
import com.crrepa.p0.j;

public class e {
    private static final int a = 165;
    public static final int b = 256;
    public static final int c = 64;

    private e() {
    }

    public static int a(String string) {
        int n = j.b(string);
        if (com.crrepa.l.a.b().h()) {
            return com.crrepa.l.a.b().c();
        }
        if (165 <= n) {
            return 256;
        }
        return 64;
    }

    public static byte[] a(byte[] byArray, int n) {
        if (!com.crrepa.l.a.b().h() && !com.crrepa.l.a.b().i()) {
            byArray = n == 64 ? e.b(byArray) : e.a(byArray);
        }
        return byArray;
    }

    private static byte[] a(byte[] byArray) {
        byte[] byArray2 = com.crrepa.c0.b.a(byArray, 65258);
        byte[] byArray3 = new byte[byArray.length + byArray2.length + 2];
        byArray3[0] = -2;
        int n = byArray2.length;
        System.arraycopy(byArray2, 0, byArray3, 1, n);
        byArray3[byArray2.length + 1] = (byte)byArray.length;
        int n2 = byArray2.length + 2;
        n = byArray.length;
        System.arraycopy(byArray, 0, byArray3, n2, n);
        return byArray3;
    }

    private static byte[] b(byte[] byArray) {
        byte[] byArray2 = com.crrepa.c0.b.a(byArray, 65258);
        byte[] byArray3 = new byte[(byte)(byArray.length + byArray2.length + 3)];
        byArray3[0] = -1;
        byArray3[1] = -1;
        int n = byArray2.length;
        System.arraycopy(byArray2, 0, byArray3, 2, n);
        byArray3[byArray2.length + 2] = (byte)byArray.length;
        int n2 = byArray2.length + 3;
        n = byArray.length;
        System.arraycopy(byArray, 0, byArray3, n2, n);
        return byArray3;
    }
}
