package org.jdbc.monitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 读取属性文件，提供取属性值
 * @author: shi rui
 * @create: 2018-12-12 11:21
 */
public class PropertyUtils {
    private static Properties properties;
    static {
        try {
            properties = new Properties();
            //先加载jar包中属性文件
            InputStream jarPropertyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc_monitor.properties");
            if(jarPropertyIS != null) {
                properties.load(jarPropertyIS);
                jarPropertyIS.close();
            }
            //再加载class path下的属性文件
            Enumeration<URL> resources = PropertyUtils.class.getClassLoader().getResources(".");
            while (resources.hasMoreElements()){
                URL url = resources.nextElement();
                File classPath = new File(url.getPath());
                if(classPath.isDirectory()){
                    File[] files = classPath.listFiles();
                    for(File file : files){
                        if(file.isDirectory() || !file.getName().endsWith("properties")){
                            continue;
                        }
                        try(InputStream inputStream = new FileInputStream(file)){
                            properties.load(inputStream);
                        }
                    }
                }

            }
        }catch (Exception e){
            throw new RuntimeException("读取属性文件出错:",e);
        }
    }


    public static String getString(String key){
        return properties.getProperty(key);
    }

    public static String getString(String key,String defaultValue){
        String value = properties.getProperty(key);
        return StringUtils.isEmpty(value)?defaultValue:value;
    }

    public static Integer getInt(String key){
        String value = getString(key);
        if(isDigit(value.toCharArray())){
            return Integer.parseInt(value);
        }else{
            throw new RuntimeException(String.format("属性[%s]对应的值[%s]不能转化为Integer",key,value));
        }
    }

    public static Long getLong(String key){
        String value = getString(key);
        if(isDigit(value.toCharArray())){
            return Long.parseLong(value);
        }else{
            throw new RuntimeException(String.format("属性[%s]对应的值[%s]不能转化为Long",key,value));
        }
    }

    public static boolean getBoolean(String key){
        String value = getString(key);
        if(isBoolean(value)){
            return Boolean.valueOf(value.toLowerCase());
        }else{
            throw new RuntimeException(String.format("属性[%s]对应的值[%s]不能转化为Boolean",key,value));
        }
    }

    private static boolean isDigit(char[] chars){
        if(chars == null || chars.length == 0 ){
            return false;
        }
        for(char c:chars){
            if(!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    private static boolean isBoolean(String value){
        if("false".equals(value.toLowerCase())){
            return true;
        }
        if("true".equals(value.toLowerCase())){
            return true;
        }
        return false;
    }
}
