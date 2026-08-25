/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.bluetooth.BluetoothGatt
 *  androidx.annotation.NonNull
 */
package com.crrepa.l;

import android.bluetooth.BluetoothGatt;
import androidx.annotation.NonNull;
import com.crrepa.ble.conn.type.CRPProtocolVersion;

public class a {
    private static final int f = 255;
    private BluetoothGatt a;
    private BluetoothGatt b;
    private BluetoothGatt c;
    private int d = 20;
    private CRPProtocolVersion e;

    private a() {
    }

    public static a b() {
        return com.crrepa.l.a$b.a;
    }

    public BluetoothGatt a() {
        return this.a;
    }

    public void a(@NonNull BluetoothGatt bluetoothGatt) {
        this.a = bluetoothGatt;
    }

    public BluetoothGatt d() {
        return this.b;
    }

    public void b(BluetoothGatt bluetoothGatt) {
        this.b = bluetoothGatt;
    }

    public BluetoothGatt f() {
        return this.c;
    }

    public void c(BluetoothGatt bluetoothGatt) {
        this.c = bluetoothGatt;
    }

    public int c() {
        return this.d;
    }

    public void a(int n) {
        if (255 < this.d) {
            n = 255;
        }
        int n2 = n + -3;
        this.d = n2 - n2 % 4;
    }

    public void j() {
        this.d = 20;
    }

    public CRPProtocolVersion e() {
        return this.e;
    }

    public void a(CRPProtocolVersion cRPProtocolVersion) {
        this.e = cRPProtocolVersion;
    }

    public boolean i() {
        return this.e == CRPProtocolVersion.V3;
    }

    public boolean h() {
        return this.e == CRPProtocolVersion.V2;
    }

    public boolean g() {
        return this.e == CRPProtocolVersion.V1;
    }

    private static class b {
        private static a a = new a();

        private b() {
        }
    }
}
