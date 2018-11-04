package com.ppandroid.openalipay;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    /**
     * 时间戳转换成日期格式字符串
     * @return
     */
    public static String getTime() {


       String format = "yyyy-MM-dd HH:mm:ss";

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

}
