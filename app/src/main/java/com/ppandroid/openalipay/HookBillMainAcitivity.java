package com.ppandroid.openalipay;


import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import static com.ppandroid.openalipay.JinGuanJiaHook.loadTaskTime2;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class HookBillMainAcitivity {

    static class Model{
        String title="";
        String bizInNo="";
        String comsumeFee="+0.01";
        String account="";
    }
    static Object reloadObj=null;
     static List<Model> saveList=new ArrayList<>();
    static XC_LoadPackage.LoadPackageParam classLoader;

     static Context systemContext;

    private static void setBroadcast() {
        final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
        systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");

        IntentFilter intentFilter = new IntentFilter();
        String SETTING_CHANGED = "name.alipay.SETTING_CHANGED2";
        intentFilter.addAction(SETTING_CHANGED);
        systemContext.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


            }
        }, intentFilter);
    }


    public static void hook(XC_LoadPackage.LoadPackageParam loadPackageParam){
        classLoader=loadPackageParam;

        final Class<?> aClass = findClass("com.alipay.mobile.bill.list.common.newList.BillListItemModel", loadPackageParam.classLoader);

        XC_MethodHook methodHook=new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args!=null&&param.args.length!=0){
                    L.log(saveList.size()+"======save---size===");
                    Object obj=param.args[0];

                    Field field=obj.getClass().getDeclaredField("consumeTitle");
                    Field field2=obj.getClass().getDeclaredField("bizInNo");
                    Field field3=obj.getClass().getDeclaredField("consumeFee");
                    Model model=new Model();
                    model.title=field.get(obj)+"";
                    model.bizInNo=field2.get(obj)+"";
                    model.comsumeFee=field3.get(obj)+"";
                    model.account=Base.account;
                    saveList.add(model);

                }
            }
        };
        XposedBridge.hookAllConstructors(aClass,methodHook);






        hookRefrash(loadPackageParam);


    

        final Class<?> bClass = findClass("com.alipay.mobile.bill.list.utils.BillCommonListView", loadPackageParam.classLoader);



        XC_MethodHook methodHook2=new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
             //   XposedHelpers.callMethod(obj,"reloadData");

            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                reloadObj=param.thisObject;
                uploadRecord();
                saveList.clear();

            }
        };
        XposedBridge.hookAllMethods(bClass,"reloadData",methodHook2);

        final Class<?> cClass = findClass("com.alipay.mobile.bill.list.ui.BillMainListActivity", loadPackageParam.classLoader);
        XC_MethodHook methodHook3=new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                obj=param.thisObject;
                L.log("====BillMainListActivity==onCreate==");
               // hookRecord(classLoader);
            }
        };
        XposedBridge.hookAllMethods(cClass,"onCreate",methodHook3);





    }
    public static boolean isNeedUpload=true;

    /**
     * 监听收款记录
     * @param loadPackageParam
     */
    private static  void hookRecord(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            XposedHelpers.findAndHookMethod("android.app.NotificationManager"
                    , loadPackageParam.classLoader, "notify"
                    , String.class, int.class, Notification.class
                    , new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            Activity activity= (Activity) obj;
                            Class name=activity.getClass();

                            Intent it=new Intent();
                            it.setClass(activity,name);
                            activity.startActivity(it);
                            activity.finish();

                        }

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            //通过param拿到第三个入参notification对象
                            Notification notification = (Notification) param.args[2];

                          /*  //获得包名
                            String aPackage = notification.contentView.getPackage();
                            L.log(aPackage+"==========="+JinGuanJia);
                            if (aPackage.equals(JinGuanJia)){

                            }*/

                            /*XposedBridge.log("methodHookParam.args:  " + Arrays.toString(param.args));
                            //通过param拿到第三个入参notification对象
                            Notification notification = (Notification) param.args[2];
                            //获得包名
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Bundle bundle = notification.extras;
                                for (String key: bundle.keySet())
                                {
                                    XposedBridge.log("Key=" + key + ", content=" +bundle.getString(key));
                                }
                            }*/
                        }
                    });
        }

    }

    private static void hookRefrash(XC_LoadPackage.LoadPackageParam loadPackageParam) {


    }

    static Object obj=null;

    static Timer timer=null;
    private static void startTimeTask() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                L.log("=$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+isNeedUpload);
                if (isNeedUpload){
                    L.log("==刷新交易记录页面================================================="+isNeedUpload);
                    Activity activity= (Activity) obj;
                    Intent it=new Intent();
                    it.setClass(activity,activity.getClass());
                    activity.startActivity(it);
                    activity.finish();
                    isNeedUpload=false;
                }

            }
        },100,loadTaskTime2);
    }


    private static Activity tempActitivty=null;
    private static void uploadRecord() {
        L.log("执行定时上传任务");
        if (!saveList.isEmpty()){
            Gson gson=new Gson();
            String ss=gson.toJson(saveList);
            String url=Base.url_upload_record+"?orders="+ss;
            L.log(url);
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new StringCallback(){

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            L.log(response);
                            if (tempActitivty!=null){
                                tempActitivty.finish();
                                tempActitivty=null;
                            }

                            Activity activity= (Activity) obj;
                            tempActitivty=activity;


                        }
                    });

        }


    }

}
