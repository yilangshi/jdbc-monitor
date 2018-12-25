package org.jdbc.monitor.proxy;

import java.lang.reflect.Proxy;
import java.sql.Statement;

/**
 * @author: shi rui
 * @create: 2018-12-25 11:13
 */
public class StatementFactory {

    public static <E extends Statement> E createStatement(E statement){
        StatementProxy proxy = new StatementProxy(statement);
        return (E) Proxy.newProxyInstance(Statement.class.getClassLoader(),
                new Class[] {Statement.class },proxy);
    }

    /**
     * 创建preparestatement,callable时需要sql
     * @param statement
     * @param sql
     * @param <E>
     * @return
     */
    public static <E extends Statement> E createStatement(E statement,String sql){
        StatementProxy proxy = new StatementProxy(statement,sql);
        return (E) Proxy.newProxyInstance(Statement.class.getClassLoader(),
                new Class[] {Statement.class },proxy);
    }

}
