/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.annotation.NonNull
 */
package com.crrepa.c0;

import androidx.annotation.NonNull;
import com.crrepa.c0.d;
import com.crrepa.c0.e;
import com.crrepa.f.u1;
import com.crrepa.m.f;
import com.crrepa.p0.b;
import com.crrepa.x.a;
import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public abstract class c {
    protected static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int COUNTDOWN_INTERVAL = 1000;
    protected static final int DEFAULT_START_INDEX = 0;
    protected d mTransFileManager;
    protected int timeout = 30;
    private Timer timer;
    private int waitTime;

    public c() {
        Timer timer;
        Timer timer2 = timer;
        timer = new Timer();
        v1.timer = timer2;
        v1.waitTime = 0;
    }

    private void sendFile(int n) {
        int n2 = n;
        n = this.mTransFileManager.b();
        byte[] byArray = this.getTransBytes(n2);
        if (byArray == null) {
            b.b("transBytes is null");
            this.onTransFileError();
        } else {
            this.sendMessage(e.a(byArray, n));
        }
    }

    private void checkFileCRC(int n) {
        int n2 = this.mTransFileManager.c();
        b.c("receiveCRC: " + n);
        b.c("calcFileCrc: " + n2);
        n = n == n2 ? 1 : 0;
        this.sendFileCheckResult(n != 0);
        if (n != 0) {
            this.transComplete();
        } else {
            this.onCrcFail();
        }
    }

    private void transComplete() {
        c c2 = this;
        c2.release();
        c2.onTransComplete();
    }

    private synchronized void handleTimeout() {
        b.a("waitTime: " + this.waitTime);
        int n = this.waitTime;
        if (n < this.timeout) {
            this.waitTime = n + 1;
        } else {
            b.a("trans time out!");
            this.onTimeoutError();
        }
    }

    private synchronized void resetTimer() {
        this.waitTime = 0;
    }

    private void stopTimer() {
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
            this.timer = null;
        }
    }

    protected void createFileManager(File file, int n) {
        this.mTransFileManager = d.a(file, n);
    }

    public void setPacketLength(int n) {
        b.a("setPacketLength: " + n);
        d d2 = ((c)((Object)d2)).mTransFileManager;
        if (d2 != null) {
            d2.b(n);
        }
    }

    protected void setTransLength(int n) {
        this.mTransFileManager.c(n);
    }

    protected void release() {
        c c2 = this;
        c2.stopTimer();
        d d2 = c2.mTransFileManager;
        if (d2 != null) {
            d2.a();
            this.mTransFileManager = null;
        }
    }

    protected void startTrans() {
        long l = this.mTransFileManager.d();
        if (l < 0L) {
            this.onTransFileError();
        } else {
            c c2 = this;
            byte[] byArray = c2.getFileSizeBytes(l);
            c2.sendBleMessage(u1.a(c2.getCmd(), byArray));
            a.a().b(n -> {
                a.a().b();
                this.setPacketLength(n);
            });
        }
    }

    @NonNull
    protected byte[] getFileSizeBytes(long l) {
        return com.crrepa.p0.d.c(l);
    }

    public void transFileIndex(com.crrepa.i0.a a2) {
        if (this.mTransFileManager == null) {
            b.b("FileManager is null");
            return;
        }
        this.resetTimer();
        if (a2.d()) {
            this.checkFileCRC(a2.a());
        } else {
            int n = a2.b();
            if (n >= 0) {
                c c2 = this;
                c2.sendFile(n);
                c2.onProgressChanged(n);
            }
        }
    }

    protected byte[] getTransBytes(int n) {
        return this.mTransFileManager.a(n);
    }

    protected void onProgressChanged(int n) {
        d d2 = this.mTransFileManager;
        if (d2 == null) {
            return;
        }
        int n2 = d2.e();
        this.onTransChanged(n * 100 / n2);
    }

    protected void sendFileCheckResult(boolean bl) {
        b.a("sendFileCheckResult: " + bl);
        byte[] byArray = new byte[4];
        if (!bl) {
            Arrays.fill(byArray, (byte)-1);
        }
        c c2 = this;
        c2.sendBleMessage(u1.a(c2.getCmd(), byArray));
    }

    protected void sendBleMessage(byte[] byArray) {
        f.d().a(byArray);
    }

    protected void sendMessage(byte[] byArray) {
        block2: {
            block0: {
                block1: {
                    c c2 = f2;
                    f f2 = f.d();
                    int n = c2.getCmd();
                    if (n == -77 || n == -73 || n == -9) break block0;
                    if (n == 99) break block1;
                    if (n == 108 || n == 116) break block0;
                    break block2;
                }
                f2.d(byArray);
                break block2;
            }
            f2.c(byArray);
        }
    }

    protected void startTimer() {
        if (this.timer == null) {
            Timer timer;
            Timer timer2 = timer;
            timer = new Timer();
            this.timer = timer2;
        }
        c c2 = this;
        c2.resetTimer();
        c2.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                c.this.handleTimeout();
            }
        }, 1000L, 1000L);
    }

    public boolean isStarted() {
        return this.mTransFileManager != null;
    }

    protected abstract void onTransFileNull();

    protected abstract void onTransFileError();

    protected abstract void onTimeoutError();

    protected abstract void onCrcFail();

    protected abstract void onTransStarting();

    protected abstract void onTransChanged(int var1);

    protected abstract void onTransComplete();

    public abstract int getCmd();
}
