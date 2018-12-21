package org.jdbc.monitor.proxy;

import org.jdbc.monitor.event.EventSupport;
import org.jdbc.monitor.common.CONN_METHOD;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.type.CONN_EVENT;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.UUID;

/**
 * @author: shi rui
 * @create: 2018-12-17 16:28
 */
public class ConnectionProxy extends EventSupport implements InvocationHandler {

    private final Connection conn;
    private final long startTime;

    public ConnectionProxy(Connection conn){
        this.conn = conn;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CONN_METHOD.CLOSE.getCode().equals(methodName)) {
            return handleClose(conn, method, args);
        }
        return method.invoke(conn, args);
    }

    private Object handleClose(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        EVENT_STATE eventState = EVENT_STATE.SUCCESS;
        String errorMsg = null;
        try{
            return method.invoke(proxy,args);
        }catch (Exception e){
            eventState = EVENT_STATE.FAIL;
            errorMsg= e.getMessage();
            throw e;
        }finally {
            publishEvent(ConnectionEvent.build(conn, CONN_EVENT.CONN_CLOSE, start, System.currentTimeMillis(), eventState, errorMsg));
        }
    }

    public long getStartTime() {
        return startTime;
    }
}
