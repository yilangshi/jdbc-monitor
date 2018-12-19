package org.jdbc.monitor.event;

import org.jdbc.monitor.event.type.EventType;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.util.EventObject;

/**
 * @author: shi rui
 * @create: 2018-12-14 17:57
 */
public class ConnectionEvent extends MonitorEvent implements Serializable {

    public ConnectionEvent(Driver driver){
        super(driver);
    }

    public ConnectionEvent(Object source,EventType eventType,long generateTime, long fireTime, EVENT_STATE state, String errorMsg){
        super(source,eventType,generateTime,fireTime,state,errorMsg);
    }

    public static ConnectionEvent build(Object source,EventType eventType,long generateTime,long fireTime){
        return new ConnectionEvent(source,eventType,generateTime,fireTime,EVENT_STATE.SUCCESS,null);
    }

    public static ConnectionEvent build(Object source,EventType eventType,long generateTime,long fireTime,EVENT_STATE state,String errorMsg){
        return new ConnectionEvent(source,eventType,generateTime,fireTime,state,errorMsg);
    }

}
