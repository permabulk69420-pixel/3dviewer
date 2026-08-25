/*
 * Decompiled with CFR 0.152.
 */
package com.crrepa.c0;

import com.crrepa.c0.b;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class d {
    private static final String g = "r";
    private static final int h = -1;
    private static final int i = 4096;
    private RandomAccessFile a;
    private int b;
    private int c;
    private int d;
    private int e = -1;
    private int f;

    private d(File file, int n) {
        RandomAccessFile randomAccessFile;
        this.d = n;
        RandomAccessFile randomAccessFile2 = randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, g);
            this.a = randomAccessFile2;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            this.a();
        }
    }

    public static d a(File file, int n) {
        if (file != null && file.exists()) {
            d d2;
            d d3 = d2;
            if (new d((File)file, (int)n).a == null) {
                return null;
            }
            return d3;
        }
        return null;
    }

    public byte[] a(int n) {
        byte[] byArray;
        block14: {
            int n2;
            block13: {
                d d2 = this;
                byArray = null;
                try {
                    n2 = d2.c;
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                    return null;
                }
                n = d2.d + n * n2;
                if (-1 == this.e) break block13;
                int n3 = this.f;
                if (n3 <= n) {
                    return null;
                }
                if (n3 >= n + n2) break block13;
                n2 = n3 - n;
            }
            d d3 = this;
            int n4 = n2;
            RandomAccessFile randomAccessFile = this.a;
            randomAccessFile.seek(n);
            byte[] byArray2 = new byte[n4];
            n2 = d3.a.read(byArray2);
            if (n2 == this.c) {
                byArray = byArray2;
                break block14;
            }
            if (n2 == -1) break block14;
            byte[] byArray3 = byArray2;
            byArray = new byte[n2];
            System.arraycopy(byArray3, 0, byArray, 0, n2);
        }
        return byArray;
    }

    public byte[] a(int n, int n2) {
        block7: {
            d d2 = object;
            int n3 = n2;
            RandomAccessFile randomAccessFile = ((d)object).a;
            randomAccessFile.seek(n);
            Object object = new byte[n3];
            n = d2.a.read((byte[])object);
            if (n == n2) {
                return object;
            }
            if (n == -1) break block7;
            byte[] byArray = new byte[n];
            try {
                System.arraycopy(object, 0, byArray, 0, n);
                return byArray;
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return null;
    }

    public void a() {
        block3: {
            RandomAccessFile randomAccessFile = this.a;
            if (randomAccessFile == null) break block3;
            try {
                randomAccessFile.close();
                this.a = null;
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public int d() {
        int n = this.e;
        if (-1 != n) {
            return n;
        }
        try {
            return (int)(this.a.length() - (long)this.d);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return -1;
        }
    }

    public int e() {
        return this.b + 1;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int c() {
        byte[] byArray;
        int n;
        try {
            int n2;
            d d2 = this;
            d2.a.seek(this.d);
            n = 65258;
            int n3 = d2.d;
            byte[] byArray2 = new byte[4096];
            while ((n2 = this.a.read(byArray2)) != -1) {
                int n4;
                byte[] byArray3 = null;
                if (-1 != this.e) {
                    n4 = this.f;
                    if (n4 <= n3) {
                        return n;
                    }
                    if (n4 < n3 + 4096) {
                        byArray3 = new byte[n4 - n3];
                    }
                }
                if (n2 == 4096 && byArray3 == null) {
                    byArray3 = byArray2;
                } else {
                    if (byArray3 == null) {
                        byArray3 = new byte[n2];
                    }
                    n2 = 0;
                    n4 = 0;
                    int n5 = byArray3.length;
                    System.arraycopy(byArray2, n2, byArray3, n4, n5);
                }
                n3 += byArray3.length;
                byArray = com.crrepa.c0.b.a(byArray3, n);
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return -1;
        }
        {
            n = com.crrepa.p0.d.b(byArray[0], byArray[1]);
            continue;
        }
        return n;
    }

    public int b() {
        return this.c;
    }

    public void b(int n) {
        this.c = n;
        long l = this.a.length() - (long)this.d;
        long l2 = this.c;
        int n2 = (int)(l / l2);
        try {
            this.b = n2;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public void c(int n) {
        this.e = n;
        this.b = n / this.c;
        this.f = n + this.d;
    }
}
