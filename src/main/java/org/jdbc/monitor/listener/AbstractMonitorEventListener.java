package org.jdbc.monitor.listener;

import lombok.NonNull;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.event.type.EventType;

/**
 * @author: shi rui
 * @create: 2018-12-14 13:54
 */
public abstract class AbstractMonitorEventListener<E extends MonitorEvent> implements MonitorEventListener<E> {

    @NonNull
    protected final EventType eventType;

    public AbstractMonitorEventListener() {
        this.eventType = null;
    }

    public AbstractMonitorEventListener(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean supportsEventType(EventType eventType){
        return eventType == null || this.eventType.equals(eventType);
    }


}
