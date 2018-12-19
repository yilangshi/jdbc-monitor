package org.jdbc.monitor.excutor;

import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.exceptoin.SubmitEventException;
import org.jdbc.monitor.listener.MonitorEventListener;

import java.util.concurrent.Executor;

/**
 * simplification from spring SimpleApplicationEventMulticaster
 * @author: shi rui
 * @create: 2018-12-13 18:29
 */
public class SimpleMonitorEventMulticaster<E extends MonitorEvent,T extends EventType> extends AbstractMonitorEventMulticaster<E,T>{

    private static SimpleMonitorEventMulticaster instance = new SimpleMonitorEventMulticaster();

    private final TaskExecutor taskExecutor;

    private SimpleMonitorEventMulticaster(){
        taskExecutor = new TaskExecutor();
    }

    public static SimpleMonitorEventMulticaster getInstance() {
        return instance;
    }


    @Override
    public void multicastEvent(final E event, T eventType) {
        for (final MonitorEventListener<?> listener : getMonitorEventListeners(event, eventType)) {
            Executor executor = getTaskExecutor();
            if (executor != null) {
                executor.execute(() -> invokeListener(listener, event));
            }
            else {
                invokeListener(listener, event);
            }
        }
    }

    @Override
    public void multicastEvent(E event) {
        multicastEvent(event, (T)event.getEventType());
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    /**
     * Invoke the given listener with the given event.
     * @param listener the ApplicationListener to invoke
     * @param event the current event to propagate
     * @since 4.1
     */
    protected void invokeListener(MonitorEventListener listener, E event) {
        try {
            listener.onMonitorEvent(event);
        }catch (Exception ex) {
            throw new SubmitEventException("提交事件异常",ex);
        }
    }
}
