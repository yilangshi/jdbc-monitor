package org.jdbc.monitor.event;

import org.jdbc.monitor.event.type.STATEMENT_EVENT_TYPE;
import org.jdbc.monitor.proxy.StatementProxy;

import java.io.Serializable;
import java.sql.Driver;

/**
 * sql语句监控事件
 * @author: shi rui
 * @create: 2018-12-25 10:59
 */
public class StatementEvent extends MonitorEvent<STATEMENT_EVENT_TYPE> implements Serializable {

    private Object result;

    public StatementEvent(StatementProxy source,Object result){
        super(source);
        this.result = result;
    }

    public StatementEvent(StatementProxy source, Object result, STATEMENT_EVENT_TYPE eventType,long generateTime, long fireTime, EVENT_STATE state, String errorMsg){
        super(source, eventType, generateTime, fireTime, state, errorMsg);
        this.result = result;
    }

    public static StatementEvent build(StatementProxy source, Object result, STATEMENT_EVENT_TYPE eventType,long generateTime,long fireTime){
        return new StatementEvent(source, result, eventType, generateTime, fireTime, EVENT_STATE.SUCCESS,null);
    }

    public static StatementEvent build(StatementProxy source, Object result, STATEMENT_EVENT_TYPE eventType,long generateTime,long fireTime,EVENT_STATE state,String errorMsg){
        return new StatementEvent(source, result, eventType, generateTime, fireTime, state, errorMsg);
    }

    public Object getResult() {
        return result;
    }
}
