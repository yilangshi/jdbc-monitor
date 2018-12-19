package org.jdbc.monitor.proxy;

import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author: shi rui
 * @create: 2018-12-17 17:43
 */
public class ConnectionFactory {

    public static Connection createConnection(Connection connection){
        ConnectionProxy proxy = new ConnectionProxy(connection);
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class[] {Connection.class },proxy);
    }
}
