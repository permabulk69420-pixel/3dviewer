/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 */
package com.crrepa.p0;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class j {
    private static final String a = "-";
    private static final int b = 3;
    private static final int c = 1;
    private static final int d = 2;

    private j() {
    }

    public static String a(String stringArray) {
        if ((stringArray = j.a((String)stringArray, a)) != null && stringArray.length >= 3) {
            return stringArray[1];
        }
        return null;
    }

    public static int b(String string) {
        int n = 0;
        Object object = j.a(string, a);
        if (object != null && ((String[])object).length >= 3) {
            ArrayList<Integer> arrayList;
            object = Pattern.compile("\\d+").matcher(object[2]);
            ArrayList<Integer> arrayList2 = arrayList;
            arrayList = new ArrayList<Integer>();
            while (((Matcher)object).find()) {
                arrayList2.add(Integer.valueOf(((Matcher)object).group(0)));
            }
            object = arrayList2.iterator();
            while (object.hasNext()) {
                int n2 = n;
                n = (Integer)object.next();
                n = n2 * 10 + n;
            }
            return n;
        }
        return n;
    }

    private static String[] a(String string, String string2) {
        if (TextUtils.isEmpty((CharSequence)string)) {
            return null;
        }
        return string.split(string2);
    }
}
