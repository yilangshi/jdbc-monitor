package org.jdbc.monitor.proxy;

import org.jdbc.monitor.event.EventSupport;
import org.jdbc.monitor.common.CONN_METHOD;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.type.CONN_EVENT_TYPE;
import org.jdbc.monitor.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

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
        CONN_METHOD connMethod = CONN_METHOD.getConnMethod(method.getName());
        if(connMethod != null) {
            return doHandler(connMethod, method, args);
        }
        return method.invoke(args);
    }

    private Object doHandler(CONN_METHOD connMethod, Method method, Object[] args) throws Throwable{
        switch (connMethod) {
            case CLOSE:
                return handleCommInvoke(method, args, CONN_EVENT_TYPE.CONN_CLOSE);
            case CREATE_STATEMENT:
                return handleCreateStatement(method, args, CONN_EVENT_TYPE.CONN_CREATE_STATEMENT);
            case CREATE_PREPARED_STATEMENT:
                return handleCreatePrepareStatement(method, args, CONN_EVENT_TYPE.CONN_CREATE_PREPARED_STATEMENT);
            case CREATE_CALLABLE_STATEMENT:
                return handleCreateCallableStatement(method, args, CONN_EVENT_TYPE.CONN_CREATE_CALLABLE_STATEMENT);
            case CREATE_CLOB:
                return handleCommInvoke(method, args, CONN_EVENT_TYPE.CONN_CREATE_CLOB);
            case CREATE_BLOB:
                return handleCommInvoke(method, args, CONN_EVENT_TYPE.CONN_CREATE_BLOB);
            case COMMIT:
                return handleCommInvoke(method, args, CONN_EVENT_TYPE.CONN_COMMIT);
            case ROLLBACK:
                return handleCommInvoke(method, args, CONN_EVENT_TYPE.CONN_ROLLBACK);
            default:
                return method.invoke(args);
        }
    }

    private Object handleCreateStatement(Method method, Object[] args, CONN_EVENT_TYPE eventType) throws Throwable{
        Object object = handleCommInvoke(method, args, eventType);
        Assert.notNull(object,"创建Statement出错");
        return StatementFactory.createStatement((Statement)object);
    }

    private Object handleCreatePrepareStatement(Method method, Object[] args, CONN_EVENT_TYPE eventType) throws Throwable{
        Object object = handleCommInvoke(method, args, eventType);
        Assert.notNull(object,"创建PreparedStatement出错");
        return StatementFactory.createStatement((PreparedStatement)object,(String)args[0]);
    }

    private Object handleCreateCallableStatement(Method method, Object[] args, CONN_EVENT_TYPE eventType) throws Throwable{
        Object object = handleCommInvoke(method, args, eventType);
        Assert.notNull(object,"创建CallableStatement出错");
        return StatementFactory.createStatement((CallableStatement)object,(String)args[0]);
    }


    private Object handleCommInvoke(Method method, Object[] args, CONN_EVENT_TYPE eventType) throws Throwable{
        long start = System.currentTimeMillis();
        EVENT_STATE eventState = EVENT_STATE.SUCCESS;
        String errorMsg = null;
        try{
            return method.invoke(conn,args);
        }catch (Exception e){
            eventState = EVENT_STATE.FAIL;
            errorMsg= e.getMessage();
            throw e.getCause();
        }finally {
            publishEvent(ConnectionEvent.build(this, eventType, start, System.currentTimeMillis(), eventState, errorMsg));
        }
    }

    public long getStartTime() {
        return startTime;
    }
}
