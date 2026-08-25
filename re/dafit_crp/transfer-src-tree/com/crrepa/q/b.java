/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.bluetooth.BluetoothGattCharacteristic
 *  android.bluetooth.BluetoothGattService
 */
package com.crrepa.q;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import java.util.ArrayList;
import java.util.List;

public class b {
    private BluetoothGattCharacteristic a;
    private BluetoothGattCharacteristic b;
    private BluetoothGattCharacteristic c;
    private BluetoothGattCharacteristic d;
    private BluetoothGattCharacteristic e;
    private BluetoothGattCharacteristic f;
    private BluetoothGattCharacteristic g;
    private BluetoothGattCharacteristic h;
    private BluetoothGattCharacteristic i;
    private BluetoothGattCharacteristic j;
    private BluetoothGattCharacteristic k;
    private BluetoothGattCharacteristic l;
    private BluetoothGattCharacteristic m;

    public b(List<BluetoothGattService> object) {
        object = object.iterator();
        while (object.hasNext()) {
            String string;
            Object object2 = (BluetoothGattService)object.next();
            String string2 = object2.getUuid().toString().toLowerCase();
            com.crrepa.p0.b.c("serviceUuid: " + string2);
            object2 = object2.getCharacteristics();
            if (string2.contains("feea")) {
                object2 = object2.iterator();
                while (object2.hasNext()) {
                    string2 = (BluetoothGattCharacteristic)object2.next();
                    string = string2.getUuid().toString().toLowerCase();
                    if (string.contains("fee1")) {
                        this.a = string2;
                        continue;
                    }
                    if (string.contains("fee2")) {
                        this.b = string2;
                        continue;
                    }
                    if (string.contains("fee3")) {
                        this.c = string2;
                        continue;
                    }
                    if (string.contains("fee5")) {
                        this.h = string2;
                        continue;
                    }
                    if (string.contains("fee6")) {
                        this.i = string2;
                        continue;
                    }
                    if (string.contains("fee7")) {
                        this.j = string2;
                        continue;
                    }
                    if (!string.contains("fee8")) continue;
                    this.k = string2;
                }
                continue;
            }
            if (string2.contains("180a")) {
                object2 = object2.iterator();
                while (object2.hasNext()) {
                    string2 = (BluetoothGattCharacteristic)object2.next();
                    string = string2.getUuid().toString().toLowerCase();
                    if (string.contains("2a28")) {
                        this.e = string2;
                        continue;
                    }
                    if (string.contains("2a29")) {
                        this.l = string2;
                        continue;
                    }
                    if (!string.contains("2a24")) continue;
                    this.f = string2;
                }
                continue;
            }
            if (string2.contains("180f")) {
                object2 = object2.iterator();
                while (object2.hasNext()) {
                    string2 = (BluetoothGattCharacteristic)object2.next();
                    if (!string2.getUuid().toString().toLowerCase().contains("2a19")) continue;
                    this.d = string2;
                }
                continue;
            }
            if (string2.contains("180d")) {
                object2 = object2.iterator();
                while (object2.hasNext()) {
                    string2 = (BluetoothGattCharacteristic)object2.next();
                    if (!string2.getUuid().toString().contains("2a37")) continue;
                    this.g = string2;
                }
                continue;
            }
            if (!string2.contains("3802")) continue;
            object2 = object2.iterator();
            while (object2.hasNext()) {
                string2 = (BluetoothGattCharacteristic)object2.next();
                if (!string2.getUuid().toString().contains("4a02")) continue;
                this.m = string2;
            }
        }
    }

    public BluetoothGattCharacteristic i() {
        return this.a;
    }

    public BluetoothGattCharacteristic m() {
        return this.b;
    }

    public BluetoothGattCharacteristic f() {
        return this.c;
    }

    public BluetoothGattCharacteristic a() {
        return this.d;
    }

    public BluetoothGattCharacteristic b() {
        return this.e;
    }

    public BluetoothGattCharacteristic c() {
        return this.f;
    }

    public BluetoothGattCharacteristic e() {
        return this.g;
    }

    public BluetoothGattCharacteristic k() {
        return this.h;
    }

    public BluetoothGattCharacteristic j() {
        return this.i;
    }

    public BluetoothGattCharacteristic d() {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.j;
        if (bluetoothGattCharacteristic != null) {
            return bluetoothGattCharacteristic;
        }
        return this.k;
    }

    public BluetoothGattCharacteristic h() {
        return this.l;
    }

    public BluetoothGattCharacteristic l() {
        return this.m;
    }

    public List<BluetoothGattCharacteristic> g() {
        ArrayList<BluetoothGattCharacteristic> arrayList;
        ArrayList<BluetoothGattCharacteristic> arrayList2 = arrayList;
        arrayList2();
        arrayList2.add(this.c);
        arrayList2.add(this.g);
        arrayList2.add(this.j);
        arrayList2.add(this.k);
        arrayList2.add(this.a);
        arrayList.add(this.m);
        return arrayList;
    }

    public boolean n() {
        return this.k != null;
    }

    public boolean o() {
        return this.a != null && this.b != null && this.c != null;
        {
        }
    }
}
