/*
 * Decompiled with CFR 0.152.
 */
package com.crrepa.i0;

import com.crrepa.p0.b;
import com.crrepa.p0.d;

public class a {
    public static final int e = 65535;
    private byte[] a;
    private boolean b;
    private boolean c;
    private int d;

    public a(byte[] byArray) {
        a a2 = this;
        a2.b = false;
        a2.c = false;
        a2.d = -1;
        a2.a = byArray;
        a2.c();
    }

    public a(byte[] byArray, boolean bl) {
        a a2 = this;
        a2.c = false;
        a2.d = -1;
        a2.a = byArray;
        a2.b = bl;
        a2.c();
    }

    private void c() {
        byte[] byArray = this.a;
        if (this.a != null && 2 <= byArray.length) {
            if (this.b) {
                if (4 <= byArray.length) {
                    this.d = (int)com.crrepa.p0.d.e(byArray);
                }
            } else {
                this.d = com.crrepa.p0.d.b(byArray[0], byArray[1]);
                boolean bl = this.d == 65535;
                this.c = bl;
            }
        }
    }

    public int b() {
        com.crrepa.p0.b.c("trans offset: " + this.d);
        return this.d;
    }

    public int a() {
        Object object = ((a)object).a;
        if (((a)object).a.length == 4) {
            return com.crrepa.p0.d.b((byte)object[2], (byte)object[3]);
        }
        return com.crrepa.p0.d.b((byte)object[1], (byte)object[0]);
    }

    public boolean d() {
        return this.c;
    }

    public void a(boolean bl) {
        this.c = bl;
    }
}
