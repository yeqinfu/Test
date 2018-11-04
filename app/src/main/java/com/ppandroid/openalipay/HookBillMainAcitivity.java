package com.ppandroid.openalipay;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class HookBillMainAcitivity {
    static class Model{
        String title="";
        String bizInNo="";
    }
     static List<Model> saveList=new ArrayList<>();
    public static void hook(XC_LoadPackage.LoadPackageParam loadPackageParam){
        final Class<?> aClass = findClass("com.alipay.mobile.bill.list.common.newList.BillListItemModel", loadPackageParam.classLoader);

        XC_MethodHook methodHook=new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args!=null&&param.args.length!=0){

                    L.log("==========BillListItemModel====");
                    L.log(saveList.size()+"======save---size===");
                    Object obj=param.args[0];
                    Field field=obj.getClass().getDeclaredField("consumeTitle");
                    L.log("-->"+field.getName()+"="+field.get(obj));
                    Field field2=obj.getClass().getDeclaredField("bizInNo");
                    L.log("-->"+field2.getName()+"="+field2.get(obj));
                    Model model=new Model();
                    model.title=field.get(obj)+"";
                    model.bizInNo=field2.get(obj)+"";
                    saveList.add(model);

                }
            }
        };
        XposedBridge.hookAllConstructors(aClass,methodHook);




        final Class<?> bClass = findClass("com.alipay.mobile.bill.list.utils.asListView.ASListView", loadPackageParam.classLoader);

        XC_MethodHook methodHook2=new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                saveList.clear();

            }
        };
        XposedBridge.hookAllMethods(bClass,"reloadData",methodHook2);
    }
}
