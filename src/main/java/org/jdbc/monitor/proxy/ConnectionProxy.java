package org.jdbc.monitor.proxy;

import org.jdbc.monitor.event.EventSupport;
import org.jdbc.monitor.common.CONN_METHOD;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.type.CONN_EVENT_TYPE;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

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
        CONN_METHOD connMethod = CONN_METHOD.getConnMethod(methodName);
        switch (connMethod){
            case CLOSE:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_CLOSE);
            case CREATE_STATEMENT:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_CREATE_STATEMENT);
            case CREATE_PREPARED_STATEMENT:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_CREATE_PREPARED_STATEMENT);
            case CREATE_CALLABLE_STATEMENT:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_CREATE_CALLABLE_STATEMENT);
            case CREATE_CLOB:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_CREATE_CLOB);
            case CREATE_BLOB:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_CREATE_BLOB);
            case COMMIT:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_COMMIT);
            case ROLLBACK:
                return handleCommInvoke(conn, method, args, CONN_EVENT_TYPE.CONN_ROLLBACK);
            default:return method.invoke(conn, args);
        }
    }

    private Object handleCommInvoke(Object proxy, Method method, Object[] args, CONN_EVENT_TYPE eventType) throws Throwable{
        long start = System.currentTimeMillis();
        EVENT_STATE eventState = EVENT_STATE.SUCCESS;
        String errorMsg = null;
        try{
            return method.invoke(proxy,args);
        }catch (Exception e){
            eventState = EVENT_STATE.FAIL;
            errorMsg= e.getMessage();
            throw e.getCause();
        }finally {
            publishEvent(ConnectionEvent.build(conn, eventType, start, System.currentTimeMillis(), eventState, errorMsg));
        }
    }

    public long getStartTime() {
        return startTime;
    }
}
