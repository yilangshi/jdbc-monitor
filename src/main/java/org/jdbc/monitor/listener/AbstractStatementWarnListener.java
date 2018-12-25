package org.jdbc.monitor.listener;

import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.StatementEvent;
import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.event.type.STATEMENT_EVENT_TYPE;
import org.jdbc.monitor.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shi rui
 * @create: 2019-01-17 15:10
 */
public abstract class AbstractStatementWarnListener<E extends Warn> extends AbstractMonitorEventListener<StatementEvent> implements WarnListener<E>{


    List<E> warnList = new ArrayList<>();

    public AbstractStatementWarnListener(final List<E> warnList){
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
    public abstract void onMonitorEvent(StatementEvent event);

    @Override
    public boolean supportsEventType(EventType eventType){
        return (this.eventType !=null && this.eventType.equals(eventType)) || ClassUtils.isAssignable(STATEMENT_EVENT_TYPE.class,eventType.getClass());
    }
}
