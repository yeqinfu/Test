package com.ppandroid.openalipay.hookbundle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ppandroid.openalipay.L;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BundleHook {
    public BundleHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        String clsStr="android.app.activity";
        String fgStr="android.support.v4.app.Fragment";
        Class aClass=XposedHelpers.findClass(clsStr,loadPackageParam.classLoader);
        Class bClass=XposedHelpers.findClass(fgStr,loadPackageParam.classLoader);
        XposedHelpers.findAndHookMethod(aClass,"onCreate",Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Bundle b= (Bundle) param.args[0];
                printBundle(b);
                
            }
        });
        XposedHelpers.findAndHookMethod(bClass,"onCreateView",LayoutInflater.class,ViewGroup.class,Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Fragment fg= (Fragment) param.thisObject;
                L.log("----------fragment-----------");
                printBundle(fg.getArguments());
                L.log("----------fragment--end---------");
            }
        });

    }

    private void printBundle(Bundle b) {
        L.log("============start==========");
        for (String key: b.keySet())
        {
            L.log( "Key=" + key + ", content=" +b.getString(key));
        }
        L.log("============end==========");

    }
}
