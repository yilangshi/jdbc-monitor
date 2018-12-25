package org.jdbc.monitor.listener;

import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.type.CONN_EVENT_TYPE;
import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:50
 */
public abstract class AbstractConnectWarnListener<E extends Warn> extends AbstractMonitorEventListener<ConnectionEvent> implements WarnListener<E> {

    List<E> warnList = new ArrayList<>();

    public AbstractConnectWarnListener(final List<E> warnList){
        this.warnList = warnList;
    }

    @Override
    public void addWarn(E warn) {
        warnList.add(warn);
    }

    @Override
    public void alert(String msg) {
        for(Warn warn:warnList){
            warn.alert(msg);
        }
    }

    @Override
    public abstract void onMonitorEvent(ConnectionEvent event);

    @Override
    public boolean supportsEventType(EventType eventType){
        return (this.eventType !=null && this.eventType.equals(eventType)) || ClassUtils.isAssignable(CONN_EVENT_TYPE.class,eventType.getClass());
    }
}
