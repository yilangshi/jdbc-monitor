package org.jdbc.monitor.event;

import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.excutor.SimpleMonitorEventMulticaster;
import org.jdbc.monitor.util.Assert;

/**
 * @author: shi rui
 * @create: 2018-12-17 15:10
 */
public class EventSupport {

    SimpleMonitorEventMulticaster monitorEventMulticaster;

    public EventSupport(){
        monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
    }

    public void publishEvent(MonitorEvent monitorEvent){
        Assert.notNull(monitorEvent,"monitorEvent must not null");
        monitorEventMulticaster.multicastEvent(monitorEvent,monitorEvent.getEventType());
    }

    public void publishEvent(MonitorEvent monitorEvent, EventType eventType){
        monitorEventMulticaster.multicastEvent(monitorEvent,eventType);
    }

}
