package org.jdbc.monitor.listener;

import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.event.type.CONN_EVENT;
import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.util.ClassUtils;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:59
 */
public abstract class AbstractConnectionMonitorEventListener extends AbstractMonitorEventListener<ConnectionEvent> {

    @Override
    public abstract void onMonitorEvent(ConnectionEvent event);

    @Override
    public boolean supportsEventType(EventType eventType){
        return (this.eventType !=null && this.eventType.equals(eventType)) || ClassUtils.isAssignable(CONN_EVENT.class,eventType.getClass());
    }

}
