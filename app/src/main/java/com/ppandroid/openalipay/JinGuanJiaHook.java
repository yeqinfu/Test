package com.ppandroid.openalipay;


import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by yeqinfu on 5/3/2018. 金管家内置计步器修改
 */

public class JinGuanJiaHook implements IXposedHookLoadPackage {
	public static String JinGuanJia	= "com.eg.android.AlipayGphone";

	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

	    setBroadcast();
        if (loadPackageParam.packageName.equals(JinGuanJia)) {//
                XposedBridge.log("hook支付宝===============" + loadPackageParam.packageName);
                //hookRecord(loadPackageParam);
                hook(loadPackageParam);
                hook3(loadPackageParam);
                startTimeTask();

		}
	}
    Timer timer=null;
    private void startTimeTask() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadTask();
            }
        },100,2000);
    }

    private void loadTask() {
        /*当前账号*/
        String inAlipayAccount="1366";


    }

    /**
     * 设定任务广播
     */
    private void setBroadcast() {
        final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
        final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");

        IntentFilter intentFilter = new IntentFilter();
        String SETTING_CHANGED = "name.alipay.SETTING_CHANGED";
        intentFilter.addAction(SETTING_CHANGED);
        systemContext.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isStart = intent.getExtras().getBoolean("isStart", false);
                if (isStart){
                    defaultAmount=intent.getExtras().getString("defaultAmount","0.01");
                    defaultBeizhu=intent.getExtras().getString("defaultBeizhu","defaultBeizhu");
                    XposedHelpers.callMethod(obj,"a");
                }


            }
        }, intentFilter);
    }

    /**
     * 监听收款记录
     * @param loadPackageParam
     */
    private void hookRecord(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            XposedHelpers.findAndHookMethod("android.app.NotificationManager"
                    , loadPackageParam.classLoader, "notify"
                    , String.class, int.class, Notification.class
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("methodHookParam.args:  " + Arrays.toString(param.args));
                            //通过param拿到第三个入参notification对象
                            Notification notification = (Notification) param.args[2];
                            //获得包名
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Bundle bundle = notification.extras;
                                for (String key: bundle.keySet())
                                {
                                    XposedBridge.log("Key=" + key + ", content=" +bundle.getString(key));
                                }
                            }
                        }
                    });
        }

    }

    private void hook2(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final Class<?> sensorEL = findClass("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", loadPackageParam.classLoader);
        XC_MethodHook dd=new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Field g=param.thisObject.getClass().getDeclaredField("localConsultSetAmountReq");
                    g.setAccessible(true);
                    Field kk=g.getClass().getDeclaredField("qrCodeUrl");
                    String sss= (String) kk.get(param.thisObject);
                    XposedBridge.log("=====&&&&&&&&&&&&&&&&&&============="+sss);
                } catch (NoSuchFieldException e) {
                    XposedBridge.log("======NoSuchFieldException:localConsultSetAmountReq==============");
                    e.printStackTrace();
                }
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param)  {

                    try {
                        Field g=param.thisObject.getClass().getDeclaredField("g");
                        g.setAccessible(true);

                        try {
                            g.set(param.thisObject,"0.01");
                        } catch (IllegalAccessException e) {
                            XposedBridge.log("======*****IllegalAccessException********1008==============");
                            e.printStackTrace();
                        }
                        XposedBridge.log("======*************1008==============");



                    } catch (NoSuchFieldException e) {
                        XposedBridge.log("======NoSuchFieldException:localConsultSetAmountReq==============");
                        e.printStackTrace();
                    }
                }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "a",dd);




    }

    Set<XC_MethodHook.Unhook> mUnhookSet;
    private int i=0;
    private static Object sObject;
    private void hook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final Class<?> sensorEL = findClass("com.alipay.mobile.payee.ui.PayeeQRActivity", loadPackageParam.classLoader);
        XC_MethodHook cc=new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("==============onReceiveMessage==============");
                if (param.args!=null&&param.args.length!=0){
                    Object obj=param.args[0];
                    Class target=obj.getClass();
                    Field e1=target.getDeclaredField("msgData");
                    Field e2=target.getDeclaredField("pushData");
                    Field e3=target.getDeclaredField("biz");
                    String s= (String) e1.get(obj);
                    s=s+"======="+e2.get(obj);
                    s=s+"======="+e3.get(obj);

                    XposedBridge.log("====R**********="+s);
                }

            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "onReceiveMessage",cc);

        XC_MethodHook dd=new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                sObject = param.thisObject;
                Intent paramIntent= (Intent) param.args[2];

                XposedBridge.log("qr_money"+paramIntent.getStringExtra("qr_money"));
                XposedBridge.log("beiZhu"+paramIntent.getStringExtra("beiZhu"));
                XposedBridge.log("qrCodeUrl"+paramIntent.getStringExtra("qrCodeUrl"));
                XposedBridge.log("qrCodeUrlOffline"+paramIntent.getStringExtra("qrCodeUrlOffline"));



            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "onActivityResult",dd);

    }

    private static  String defaultAmount="0.01";
    private static String defaultBeizhu="default msg";
    private static Object obj;
    private void hook3(final XC_LoadPackage.LoadPackageParam loadPackageParam){
        final Class<?> sensorEL = findClass("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", loadPackageParam.classLoader);



        //hook结果
        hookResultA(loadPackageParam);


        //==============hook 金额================
        XC_MethodHook dd=new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param)  {
                obj=param.thisObject;
                XposedBridge.log("======afterHookedMethod============="+defaultAmount);
                //========================hook 金额===============================
                try {
                    XposedBridge.log("======hook金额============"+defaultAmount);
                    Field g=param.thisObject.getClass().getDeclaredField("g");
                    g.setAccessible(true);

                    try {
                        g.set(param.thisObject,defaultAmount);
                    } catch (IllegalAccessException e) {
                        XposedBridge.log("======金额被hook了============="+defaultAmount);
                        e.printStackTrace();
                    }

                } catch (NoSuchFieldException e) {
                    XposedBridge.log("======hoooook金额报错了=============");
                    e.printStackTrace();
                }
                //=====================hook 备注===================================
                XposedBridge.log("=====hook 备注=============");
                 hookBeizhu(loadPackageParam,defaultBeizhu);
                //=====================hook finish 让保持在这个页面===================================

                hookFinish();



                //==============hook自动执行确定按钮=============================

                XposedBridge.log("======hook金额备注执行完毕=============");

                //===============解除对finish的hook===============
             //   XC_MethodHook.Unhook()

            }

            private void hookFinish() {
                final Class<?> sensorEL = findClass("com.alipay.mobile.framework.app.ui.BaseActivity", loadPackageParam.classLoader);
                XC_MethodReplacement finishMethod=new XC_MethodReplacement() {


                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("================finishfinishfinish===hook 结果================");

                        return null;
                    }
                };

                XposedBridge.hookAllMethods(sensorEL,"finish",finishMethod);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param)  {
            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "onCreate",dd);



    }

    private void hookResultA(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final Class<?> sensorEL = findClass("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", loadPackageParam.classLoader);
        XC_MethodHook dd=new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args==null||param.args.length==0){
                    return;
                }
                Object obj=param.args[0];
                Class target=obj.getClass();
                Field f=target.getDeclaredField("qrCodeUrl");
                Field f2=target.getDeclaredField("printQrCodeUrl");
                Field f3=target.getDeclaredField("codeId");
                String s= (String) f.get(obj);
                String s2= (String) f2.get(obj);
                String s3= (String) f3.get(obj);
                XposedBridge.log("==========qrCodeUrl=================="+s);
                XposedBridge.log("==========printQrCodeUrl=================="+s2);
                XposedBridge.log("==========codeId=================="+s3);
                Context context= (Context) param.thisObject;
                Intent it=new Intent(SendToMainAcitivity);
                it.putExtra("qrCodeUrl",s);
                context.sendBroadcast(it);


            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "a",dd);
    }
    static String SendToMainAcitivity = "name.alipay.receiver.all";

    private void hookBeizhu(XC_LoadPackage.LoadPackageParam loadPackageParam, final String beizhu) {
        final Class<?> sensorEL = findClass("com.alipay.mobile.antui.input.AUInputBox", loadPackageParam.classLoader);
        XC_MethodReplacement dd= new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return beizhu;
            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "getUbbStr",dd);
    }






}
