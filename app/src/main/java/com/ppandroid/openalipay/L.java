package com.ppandroid.openalipay;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

public class L {
    public static void log(String s){
        XposedBridge.log(s);
        Log.d("yeqinfu",s);
    }
}
