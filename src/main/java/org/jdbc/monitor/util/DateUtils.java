package org.jdbc.monitor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: shi rui
 * @create: 2018-12-21 09:24
 */
public class DateUtils {

    private static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:sss";

    public static String format(Date date, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static String format(Date date){
        return format(date,DEFAULT_PATTERN);
    }

    public static String format(long time){
        return format(time,DEFAULT_PATTERN);
    }

    public static String format(long time,String pattern){
        return format(new Date(time),DEFAULT_PATTERN);
    }

}
