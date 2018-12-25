package org.jdbc.monitor.event;

import org.jdbc.monitor.event.type.CONN_EVENT_TYPE;
import org.jdbc.monitor.event.type.EventType;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.util.EventObject;

/**
 * 连接事件
 * 当创建连接时，source为MoniterDriver
 * 当连接自身事件时，source为ConnectionProxy
 * @author: shi rui
 * @create: 2018-12-14 17:57
 */
public class ConnectionEvent extends MonitorEvent<CONN_EVENT_TYPE> implements Serializable {

    public ConnectionEvent(Object source){
        super(source);
    }

    public ConnectionEvent(Object source,CONN_EVENT_TYPE eventType,long generateTime, long fireTime, EVENT_STATE state, String errorMsg){
        super(source,eventType,generateTime,fireTime,state,errorMsg);
    }

    public static ConnectionEvent build(Object source,CONN_EVENT_TYPE eventType,long generateTime,long fireTime){
        return new ConnectionEvent(source,eventType,generateTime,fireTime,EVENT_STATE.SUCCESS,null);
    }

    public static ConnectionEvent build(Object source,CONN_EVENT_TYPE eventType,long generateTime,long fireTime,EVENT_STATE state,String errorMsg){
        return new ConnectionEvent(source,eventType,generateTime,fireTime,state,errorMsg);
    }

}
