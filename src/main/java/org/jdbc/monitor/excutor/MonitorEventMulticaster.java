package org.jdbc.monitor.excutor;

import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.listener.MonitorEventListener;

/**
 * simplification from spring ApplicationEventMulticaster
 * @author: shi rui
 * @create: 2018-12-13 19:57
 */
public interface MonitorEventMulticaster<E extends MonitorEvent,T extends EventType> {

    void addMonitorEventListener(MonitorEventListener<E> listener);

    void removeMonitorEventListener(MonitorEventListener<E> listener);

    void removeAllListeners();

    void multicastEvent(E event);

    void multicastEvent(E event, T eventType);
}
