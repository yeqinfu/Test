package com.ppandroid.openalipay;


import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by yeqinfu on 5/3/2018. 金管家内置计步器修改
 */

public class JinGuanJiaHook implements IXposedHookLoadPackage {
    public static int loadTaskTime=3000;
    public static int loadTaskTime2=5000;
	public static String JinGuanJia	= "com.eg.android.AlipayGphone";
	//启动收款记录页面
    public static String cmdStr="am start -n com.eg.android.AlipayGphone/com.alipay.mobile.bill.list.ui.BillMainListActivity";

	private static boolean isHook=false;
	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

	    setBroadcast();
        if (loadPackageParam.packageName.equals(JinGuanJia)&&!isHook) {//
                isHook=true;
                XposedBridge.log("hook支付宝===============" + loadPackageParam.packageName);
                HookLoginName.hook(loadPackageParam);
                //hookRecord(loadPackageParam);
                hook(loadPackageParam);
                hook3(loadPackageParam);
                HookBillMainAcitivity.hook(loadPackageParam);


		}
	}
    Timer timer=null;
    private void startTimeTask() {
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
                loadTask();
            }
        },100,loadTaskTime);
    }
    String inAlipayAccount="0000";
    private void loadTask() {
        /*当前账号*/
        if (inAlipayAccount.equals("0000")){
            //没设置账户，不做任何事情
            return;
        }

        final String url=Base.url_load_task+"?inAlipayAccount="+inAlipayAccount;

        L.log("执行定时拉去生成二维码任务");

        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        XposedBridge.log("执行任务返回"+Utils.getTime()+"->"+response);
                        Log.d("yeqinfu",Utils.getTime());
                        Log.d("yeqinfu",response);
                        Gson gson=new Gson();
                        final BN_QrCode bn=gson.fromJson(response,BN_QrCode.class);
                        if (bn!=null){
                            if (bn.getMessage()!=null&&!bn.getMessage().isEmpty()){
                                for (BN_QrCode.MessageBean item:bn.getMessage()) {
                                    generateQRCode(item.getOrderNo(),item.getInAmount());
                                }

                            }
                        }




                    }

                });

    }

    private void generateQRCode(String orderNo, String inAmount) {


        defaultAmount=getAmountStr(inAmount);
        defaultBeizhu=orderNo;
        XposedBridge.log("备注更新为="+defaultBeizhu+"金额更新为："+defaultAmount);
        try {
            Field fieldg= obj.getClass().getDeclaredField("g");
            fieldg.setAccessible(true);
            fieldg.set(obj,defaultAmount);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        XposedHelpers.callMethod(obj,"a");
    }

    private String getAmountStr(String inAmount) {
        try {
            int i=Integer.parseInt(inAmount);
            double r=i/100.0;
            return r+"";
        }catch (Exception e){
            e.printStackTrace();
            L.log("==========解析金额错误==============");

        }
        return "0.01";
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
                inAlipayAccount = intent.getExtras().getString("et_account", "0000");


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
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);


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
    private void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
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
                    Class cls=Class.forName("com.alipay.mobile.bill.list.ui.BillMainListActivity",true,loadPackageParam.classLoader);
                    Activity activity= (Activity) param.thisObject;
                    Intent it=new Intent();
                    it.setClass(activity,cls);
                    activity.startActivity(it);
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

    private void postPaySuccess() {
        String url=Base.url_post_pay_success+"?inAlipayAccount="+inAlipayAccount+"&inAmount="+defaultAmount;

        XposedBridge.log(url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        XposedBridge.log("付款成功保存"+Utils.getTime()+"->"+response);





                    }

                });
    }

    private   String defaultAmount="0.01";
    private  String defaultBeizhu="default msg";
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
                 hookBeizhu(loadPackageParam);
                //=====================hook finish 让保持在这个页面===================================

                hookFinish();



                //==============hook自动执行确定按钮=============================

                XposedBridge.log("======hook金额备注执行完毕=============");
              /*  //打开记录页面
                Intent it=new Intent();
                try {
                    Class c=Class.forName("com.alipay.mobile.bill.list.ui.BillMainListActivity");

                    Context context= (Context) obj;
                    it.setClass(context,c);
                    context.startActivity(it);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    L.log("打开页面报错=======");
                }*/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startTimeTask();
                    }
                }).start();


                //===============解除对finish的hook===============
             //   XC_MethodHook.Unhook()



            }

            private void hookFinish() {
                final Class<?> sensorEL = findClass("com.alipay.mobile.framework.app.ui.BaseActivity", loadPackageParam.classLoader);
                XC_MethodReplacement finishMethod=new XC_MethodReplacement() {


                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

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

      // String result=ShellUtils.execRootCmd(cmdStr);
       //L.log("cmd result:"+result);


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
                Field f4=target.getSuperclass().getDeclaredField("message");

                String s= (String) f.get(obj);
                String s2= (String) f2.get(obj);
                String s3= (String) f3.get(obj);
                Class sup=target.getSuperclass();
                String s4= (String) f4.get(obj);

                XposedBridge.log("==========qrCodeUrl=================="+s);
                XposedBridge.log("==========printQrCodeUrl=================="+s2);
                XposedBridge.log("==========codeId=================="+s3);
                XposedBridge.log("==========message=================="+s4);

                Context context= (Context) param.thisObject;
                Intent it=new Intent(SendToMainAcitivity);
                it.putExtra("qrCodeUrl",s);
                context.sendBroadcast(it);
                //生成完二维码之后提交生成的二维码
                postGeneratedQrCode(s);


            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "a",dd);
    }

    private void postGeneratedQrCode(String qrCode) {

        String url=Base.url_post_qrcode+"?inAlipayAccount="+inAlipayAccount+"&orderNo="+defaultBeizhu+"&qrcodeUrl="+qrCode;
        XposedBridge.log(url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        XposedBridge.log("保存二维码返回"+Utils.getTime()+"->"+response);





                    }

                });
    }

    static String SendToMainAcitivity = "name.alipay.receiver.all";

    private void hookBeizhu(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final Class<?> sensorEL = findClass("com.alipay.mobile.antui.input.AUInputBox", loadPackageParam.classLoader);
        XC_MethodReplacement dd= new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return defaultBeizhu;
            }
        };
        mUnhookSet=XposedBridge.hookAllMethods(sensorEL, "getUbbStr",dd);
    }






}
