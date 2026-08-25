/*
 * Decompiled with CFR 0.152.
 */
package com.crrepa.c0;

import com.crrepa.p0.d;

public class b {
    public static final int a = 65258;

    private b() {
    }

    public static byte[] a(byte[] byArray, int n) {
        if (byArray == null) {
            return d.b(65258);
        }
        for (byte by : byArray) {
            int n2 = ((n & 0xFF00) >> 8 | (n & 0xFF) << 8) ^ by & 0xFF;
            int n3 = n2 ^ (n2 & 0xFF) >> 4;
            int n4 = n3 ^ (n3 & 0xFF) << 8 << 4;
            n = n4 ^ (n4 & 0xFF) << 4 << 1;
        }
        return d.b(n);
    }
}
