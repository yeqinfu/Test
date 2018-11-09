package com.ppandroid.openalipay;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by yeqinfu on 11/6/2018.
 */

public class HookLoginName {

    public static void hook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        /*final Class<?> aClass = findClass("com.alipay.mobile.security.login.ui.RecommandAlipayUserLoginActivity", loadPackageParam.classLoader);

        XC_MethodHook dd=new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
               if (param.args!=null&&param.args.length!=0){
                   TestUtils.reflex(param.args[0]);
               }
            }
        };
        XposedHelpers.findAndHookMethod(aClass,"onLoginResponse",dd);*/
    }
}
