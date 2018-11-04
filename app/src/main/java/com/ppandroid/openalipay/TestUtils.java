package com.ppandroid.openalipay;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestUtils {
    public static void reflex(Object obj){
        Class<?> aClass = obj.getClass();
        // aClass.getFields() 获取 public 类型的成员
        Field[] declaredFields = aClass.getDeclaredFields(); //获取所有成员包含 private
        Method[] methods = aClass.getMethods();
        for (Method mt:
                methods) {
            String mn = mt.getName();
            Object dv = mt.getDefaultValue();
            String returnType = mt.getReturnType().getName();
            L.log("方法名称：" + mn + "    默认值：" + dv + "    返回类型：" + returnType);
        }
        for (Field field:
                declaredFields) {
            try {
                field.setAccessible(true);
                String fn = field.getName();
                L.log("属性名称：" + fn + "   field.get(obj)= " + field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
