package org.jdbc.monitor;

import org.jdbc.monitor.listener.Warn;
import org.jdbc.monitor.util.ClassUtils;
import org.jdbc.monitor.warn.tool.ConsoleWarn;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author: shi rui
 * @create: 2018-12-21 09:19
 */
public class MainTest {

    public static int test(){
        int a = 10;
        try {
            return a = 20;
        }finally {
            System.out.println("++++++++++++"+a);
        }
    }

    public static Object get(){
        int i = 10;
        return i;
    }

    public static void main(String[] args){
        try {
            Class clazz = Class.forName("org.jdbc.monitor.warn.tool.ConsoleWarn");
            Object object = clazz.newInstance();
            System.out.println(ClassUtils.isAssignableValue(Warn.class,object));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
