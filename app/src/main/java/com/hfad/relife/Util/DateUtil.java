package com.hfad.relife.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 18359 on 2017/11/20.
 */

public class DateUtil {
    public static String formatDateTime() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static String time(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format((new Date(System.currentTimeMillis())));
    }
}
