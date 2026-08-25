/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.crrepa.ble.R$string
 */
package com.crrepa.i0;

import com.crrepa.ble.R;
import com.crrepa.ble.conn.listener.CRPBleFirmwareUpgradeListener;
import com.crrepa.c0.c;
import com.crrepa.c0.e;
import java.io.File;

public class b
extends c {
    private CRPBleFirmwareUpgradeListener a;
    private File b;

    private b() {
    }

    public static b a() {
        return com.crrepa.i0.b$b.a;
    }

    private void a(boolean bl) {
        CRPBleFirmwareUpgradeListener cRPBleFirmwareUpgradeListener = this.a;
        if (cRPBleFirmwareUpgradeListener != null) {
            cRPBleFirmwareUpgradeListener.onError(23, com.crrepa.p0.e.a().getString(R.string.dfu_status_error_msg));
        }
        if (bl) {
            this.sendFileCheckResult(false);
        }
        this.release();
    }

    public void a(CRPBleFirmwareUpgradeListener cRPBleFirmwareUpgradeListener) {
        this.a = cRPBleFirmwareUpgradeListener;
    }

    public void a(File file) {
        this.b = file;
    }

    public void b() {
        b b2 = this;
        b2.createFileManager(b2.b, 0);
        String string2 = com.crrepa.p0.e.c();
        com.crrepa.p0.b.c("firmwareVersion: " + string2);
        b2.setPacketLength(e.a(string2));
        if (b2.mTransFileManager != null) {
            b b3 = this;
            b3.a.onUpgradeProgressStarting(true);
            b3.startTrans();
            b3.startTimer();
        } else {
            this.a(false);
        }
    }

    public void abort() {
        b b2 = this;
        b2.sendFileCheckResult(false);
        b2.release();
    }

    @Override
    protected void onTransFileNull() {
        this.a(true);
    }

    @Override
    protected void onTransFileError() {
        this.a(true);
    }

    @Override
    protected void onTimeoutError() {
        this.a(true);
    }

    @Override
    protected void onCrcFail() {
        this.a(false);
    }

    @Override
    protected void onTransStarting() {
    }

    @Override
    protected void onTransChanged(int n) {
        CRPBleFirmwareUpgradeListener cRPBleFirmwareUpgradeListener = ((b)((Object)cRPBleFirmwareUpgradeListener)).a;
        if (cRPBleFirmwareUpgradeListener != null) {
            cRPBleFirmwareUpgradeListener.onUpgradeProgressChanged(n, 1.0f);
        }
    }

    @Override
    protected void onTransComplete() {
        CRPBleFirmwareUpgradeListener cRPBleFirmwareUpgradeListener = ((b)((Object)cRPBleFirmwareUpgradeListener)).a;
        if (cRPBleFirmwareUpgradeListener != null) {
            cRPBleFirmwareUpgradeListener.onUpgradeCompleted();
        }
    }

    @Override
    public int getCmd() {
        return 99;
    }

    private static class b {
        private static b a = new b();

        private b() {
        }
    }
}
