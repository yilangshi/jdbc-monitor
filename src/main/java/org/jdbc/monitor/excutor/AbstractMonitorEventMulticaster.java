package org.jdbc.monitor.excutor;

import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.listener.MonitorEventListener;
import org.jdbc.monitor.util.ObjectUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simplification from spring AbstractApplicationEventMulticaster
 * @author: shi rui
 * @create: 2018-12-13 19:55
 */
public abstract class AbstractMonitorEventMulticaster<E extends MonitorEvent,T extends EventType> implements MonitorEventMulticaster<E,T> {

    private final ListenerRetriever defaultRetriever = new ListenerRetriever();
    /** 临时读取 */
    final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);
    /** 全局锁 */
    private Object retrievalMutex = this.getClass();

    @Override
    public void addMonitorEventListener(MonitorEventListener<E> listener) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.monitorEventListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    @Override
    public void removeMonitorEventListener(MonitorEventListener<E> listener) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.monitorEventListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    @Override
    public void removeAllListeners() {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.monitorEventListeners.clear();
            this.retrieverCache.clear();
        }
    }

    /**
     * Return a Collection containing all MonitorEventListeners.
     * @return a Collection of MonitorEventListeners
     */
    protected Collection<MonitorEventListener<E>> getMonitorEventListeners() {
        synchronized (this.retrievalMutex) {
            return this.defaultRetriever.getMonitorEventListeners();
        }
    }

    /**
     * Return a Collection of MonitorEventListeners matching the given
     * event type. Non-matching listeners get excluded early.
     * @param event the event to be propagated. Allows for excluding
     * non-matching listeners early, based on cached matching information.
     * @param eventType the event type
     * @return a Collection of MonitorEventListeners
     */
    protected Collection<MonitorEventListener<E>> getMonitorEventListeners(
            MonitorEvent event, EventType eventType) {

        Object source = event.getSource();
        Class<?> sourceType = (source != null ? source.getClass() : null);
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

        // Quick check for existing entry on ConcurrentHashMap...
        ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getMonitorEventListeners();
        }
        //加载缓冲数据
        synchronized (this.retrievalMutex) {
            retriever = this.retrieverCache.get(cacheKey);
            if (retriever != null) {
                return retriever.getMonitorEventListeners();
            }
            retriever = new ListenerRetriever();
            Collection<MonitorEventListener<E>> listeners =
                    retrieveMonitorEventListeners(eventType, sourceType, retriever);
            this.retrieverCache.put(cacheKey, retriever);
            return listeners;
        }
    }

    /**
     * Actually retrieve the monitor listeners for the given event and source type.
     * @param eventType the event type
     * @param sourceType the event source type
     * @param retriever the ListenerRetriever, if supposed to populate one (for caching purposes)
     * @return the pre-filtered list of application listeners for the given event and source type
     */
    private Collection<MonitorEventListener<E>> retrieveMonitorEventListeners(
            EventType eventType, Class<?> sourceType, ListenerRetriever retriever) {
        Set<MonitorEventListener<?>> listeners;
        synchronized (this.retrievalMutex) {
            listeners = new LinkedHashSet<>(this.defaultRetriever.monitorEventListeners);
        }
        for (MonitorEventListener<?> listener : listeners) {
            if (supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.monitorEventListeners.add(listener);
                }
            }
        }
        return retriever.monitorEventListeners;
    }

    protected boolean supportsEvent(
            MonitorEventListener<?> listener, EventType eventType, Class<?> sourceType) {
        return listener.supportsEventType(eventType) && listener.supportsSourceType(sourceType);
    }

    /**
     * Cache key for ListenerRetrievers, based on event type and source type.
     */
    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {

        private final EventType eventType;

        private final Class<?> sourceType;

        public ListenerCacheKey(EventType eventType, Class<?> sourceType) {
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            ListenerCacheKey otherKey = (ListenerCacheKey) other;
            return (this.eventType.equals(otherKey.eventType) &&
                    ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType));
        }

        @Override
        public int hashCode() {
            return this.eventType.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
        }

        @Override
        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
        }

        @Override
        public int compareTo(ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.sourceType == null) {
                    return (other.sourceType == null ? 0 : -1);
                }
                if (other.sourceType == null) {
                    return 1;
                }
                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }
            return result;
        }
    }


    /**
     * Helper class that encapsulates a specific set of target listeners,
     * allowing for efficient retrieval of pre-filtered listeners.
     * <p>An instance of this helper gets cached per event type and source type.
     */
    private class ListenerRetriever<E extends MonitorEvent> {

        public final Set<MonitorEventListener<E>> monitorEventListeners = new LinkedHashSet<>();

        public Collection<MonitorEventListener<E>> getMonitorEventListeners() {
            return monitorEventListeners;
        }
    }
}
