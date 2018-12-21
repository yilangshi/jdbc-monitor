package org.jdbc.monitor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: shi rui
 * @create: 2018-12-21 09:24
 */
public class DateUtils {

    public static String format(Date date, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}
