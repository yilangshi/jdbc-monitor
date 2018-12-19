package org.jdbc.monitor.listener;

import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.event.MonitorEvent;

import java.util.EventListener;

/**
 * @author: shi rui
 * @create: 2018-12-13 19:50
 */
public interface MonitorEventListener<E extends MonitorEvent> extends EventListener {

    void onMonitorEvent(E event);

    boolean supportsEventType(EventType eventType);

    default boolean supportsSourceType(Class sourceType) {
        return true;
    }



}
