package org.jdbc.monitor.listener;

import org.jdbc.monitor.event.StatementEvent;
import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.event.type.STATEMENT_EVENT_TYPE;
import org.jdbc.monitor.util.ClassUtils;

/**
 * sql语句事件监听类
 * @author: shi rui
 * @create: 2018-12-25 11:03
 */
public abstract class AbstractStatementMonitorEventListener extends AbstractMonitorEventListener<StatementEvent> {
    @Override
    public abstract void onMonitorEvent(StatementEvent event);

    @Override
    public boolean supportsEventType(EventType eventType){
        return (this.eventType !=null && this.eventType.equals(eventType)) || ClassUtils.isAssignable(STATEMENT_EVENT_TYPE.class,eventType.getClass());
    }
}
