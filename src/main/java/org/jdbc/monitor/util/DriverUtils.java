package org.jdbc.monitor.util;

import org.jdbc.monitor.common.Constant;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * @author: shi rui
 * @create: 2018-12-12 19:57
 */
public class DriverUtils {

    public static Driver getDriver(String targetDriverClass) throws ClassNotFoundException {
        Class.forName(targetDriverClass);
        Enumeration e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            Driver driver = (Driver) e.nextElement();
            if (driver.getClass().getName().equals(targetDriverClass)) {
                return driver;
            }
        }
        return null;
    }

    public static String getTargetDriverUrl(String url){
        return url.substring(Constant.URL_PREFIX.length());
    }
}
