/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.bluetooth.BluetoothGatt
 *  android.bluetooth.BluetoothGattCharacteristic
 */
package com.crrepa.m;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import com.crrepa.ble.conn.callback.CRPRemoveBondCallback;
import com.crrepa.ble.conn.listener.CRPBleSendStateListener;
import com.crrepa.f.n;
import com.crrepa.f.u1;
import com.crrepa.l.a;
import com.crrepa.m.c;
import com.crrepa.p0.b;
import com.crrepa.p0.d;
import java.util.Arrays;

public class f
extends c {
    private static final long g = 50L;
    private static final long h = 200L;
    private static final long i = 500L;
    private static final long j = 1000L;
    private static f k;
    private byte[] a = null;
    private boolean b = true;
    private int c = 0;
    private byte d;
    private CRPBleSendStateListener e;
    private CRPRemoveBondCallback f;

    private f() {
    }

    public static f d() {
        if (k == null) {
            k = new f();
        }
        return k;
    }

    private void a(byte[] byArray, int n) {
        com.crrepa.p.a a2;
        if (com.crrepa.p0.d.f(byArray)) {
            return;
        }
        com.crrepa.p.a a3 = a2;
        a2 = new com.crrepa.p.a(n, byArray);
        com.crrepa.p.c.b().a(a3);
    }

    private synchronized void a(byte[] byArray, byte by) {
        com.crrepa.p0.b.a("writeCompleted: " + this.b);
        if (!this.b) {
            return;
        }
        this.d = by;
        com.crrepa.p0.b.c("WriteCmd: " + this.d);
        this.a = byArray;
        this.b = false;
        com.crrepa.o.a.a().d();
        this.g();
    }

    @SuppressLint(value={"MissingPermission"})
    private synchronized void g() {
        int n = this.a.length - this.c;
        int n2 = u1.a();
        if (n > n2) {
            n = n2;
        } else if (n <= 0) {
            this.c();
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.e();
        BluetoothGatt bluetoothGatt = com.crrepa.l.a.b().a();
        if (bluetoothGattCharacteristic != null && bluetoothGatt != null) {
            byte[] byArray = new byte[n];
            System.arraycopy(this.a, this.c, byArray, 0, n);
            bluetoothGattCharacteristic.setValue(byArray);
            if (com.crrepa.l.a.b().g()) {
                bluetoothGattCharacteristic.setWriteType(1);
            }
            com.crrepa.p0.b.c("writeCharacteristic WriteType: " + bluetoothGattCharacteristic.getWriteType());
            boolean bl = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
            com.crrepa.p0.b.c("writeCharacteristic: " + bl);
            if (bl) {
                this.c += n;
            }
            return;
        }
        com.crrepa.m.c.a();
    }

    /*
     * Unable to fully structure code
     */
    private void c() {
        block6: {
            block5: {
                v0 = this;
                v0.f();
                var1_1 = v0.d;
                if (var1_1 == 1 || var1_1 == 2) break block5;
                if (var1_1 == 17 || var1_1 == 18 || var1_1 == 59 || var1_1 == 60) ** GOTO lbl-1000
                if (var1_1 == 103) ** GOTO lbl17
                switch (var1_1) {
                    default: {
                        var1_2 = 200L;
                        break;
                    }
                    case 51: {
                        var1_2 = 1000L;
                        break;
                    }
                    case 50: {
                        com.crrepa.o.a.a().e();
                        return;
                    }
lbl17:
                    // 1 sources

                    var1_2 = 50L;
                    break;
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: lbl-1000:
                    // 2 sources

                    {
                        var1_2 = 500L;
                        break;
                    }
                }
                break block6;
            }
            var1_2 = 0L;
        }
        this.a(1, var1_2);
    }

    private BluetoothGattCharacteristic e() {
        com.crrepa.q.b b2 = this.b();
        if (b2 == null) {
            return null;
        }
        byte by = this.d;
        return by == 1 ? b2.k() : (by == 2 ? b2.j() : (by == 3 ? b2.l() : b2.m()));
    }

    private void a(int n, long l) {
        CRPBleSendStateListener cRPBleSendStateListener;
        if (this.d == 99 && (cRPBleSendStateListener = this.e) != null) {
            cRPBleSendStateListener.onSendStateChange(n);
        }
        com.crrepa.g.a.a(new Runnable(){

            @Override
            public void run() {
                com.crrepa.p.c.b().f();
            }
        }, l);
    }

    public void d(byte[] byArray) {
        this.a(byArray, 1);
    }

    public void c(byte[] byArray) {
        this.a(byArray, 2);
    }

    public void a(byte[] byArray) {
        this.a(byArray, 0);
    }

    public void b(byte[] byArray) {
        this.a(byArray, 8);
    }

    public void e(byte[] byArray) {
        this.a(byArray, 38);
    }

    public void h(byte[] byArray) {
        this.a(byArray, (byte)1);
    }

    public void j(byte[] byArray) {
        this.a(byArray, (byte)2);
    }

    public void i(byte[] byArray) {
        this.a(byArray, (byte)3);
    }

    public void g(byte[] byArray) {
        this.a(byArray, byArray[4]);
    }

    public void f(byte[] byArray) {
        f f2 = this;
        f2.g();
        if (f2.f != null && Arrays.equals(byArray, n.a())) {
            this.f.onSuccess();
            this.f = null;
        }
    }

    public void f() {
        f f2 = this;
        f2.c = 0;
        f2.b = true;
    }

    public void a(CRPBleSendStateListener cRPBleSendStateListener) {
        this.e = cRPBleSendStateListener;
    }

    public void a(CRPRemoveBondCallback cRPRemoveBondCallback) {
        this.f = cRPRemoveBondCallback;
    }
}
