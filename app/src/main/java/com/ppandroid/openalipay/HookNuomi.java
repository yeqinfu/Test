package com.ppandroid.openalipay;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by yeqinfu on 11/27/2018.
 */

public class HookNuomi implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        hookRecord(lpparam);
    }

    /**
     * 监听收款记录
     *
     * @param loadPackageParam
     */
    private void hookRecord(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            L.log("======================nuomi==========================");
            XposedHelpers.findAndHookMethod("android.app.NotificationManager", loadPackageParam.classLoader, "notify", String.class, int.class, Notification.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                }

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //通过param拿到第三个入参notification对象

					/*  //获得包名
					  String aPackage = notification.contentView.getPackage();
					  L.log(aPackage+"==========="+JinGuanJia);
					  if (aPackage.equals(JinGuanJia)){

					  }*/

                    XposedBridge.log("methodHookParam.args:  " + Arrays.toString(param.args));
                    //通过param拿到第三个入参notification对象
                    Notification notification = (Notification) param.args[2];
                    //获得包名
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Bundle bundle = notification.extras;
                        for (String key : bundle.keySet()) {
                            XposedBridge.log("Key=" + key + ", content=" + bundle.getString(key));
                        }
                    }
                }
            });
        }

    }

}
