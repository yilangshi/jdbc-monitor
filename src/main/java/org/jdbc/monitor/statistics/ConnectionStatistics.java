package org.jdbc.monitor.statistics;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.common.STAT_CONN;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.event.type.CONN_EVENT;
import org.jdbc.monitor.event.type.EventType;
import org.jdbc.monitor.exceptoin.IllegalEventException;
import org.jdbc.monitor.listener.AbstractConnectionMonitorEventListener;
import org.jdbc.monitor.listener.AbstractMonitorEventListener;
import org.jdbc.monitor.util.ClassUtils;
import org.jdbc.monitor.warn.Warn;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author: shi rui
 * @create: 2018-12-17 15:59
 */
@Slf4j
public class ConnectionStatistics extends AbstractConnectionMonitorEventListener implements Statistics<STAT_CONN>{

    private final AtomicInteger connOpenSuccessCount = new AtomicInteger();

    private final AtomicInteger connOpenFailureCount = new AtomicInteger();

    private final AtomicInteger connCloseSuccessCount = new AtomicInteger();

    private final AtomicInteger connCloseFailureCount = new AtomicInteger();

    private final ConcurrentHashMap<String,String> connOpenFailInfo = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String,String> connCloseFailInfo = new ConcurrentHashMap<>();

    private List<Warn> warnList = new ArrayList<>();

    @Override
    public void onMonitorEvent(ConnectionEvent event) {
        log.debug("receive event:"+event.toString());
        if(event.getEventType() == CONN_EVENT.CONN_OPEN){
            doConnectOpenHandler(event);
        }else if(event.getEventType() == CONN_EVENT.CONN_CLOSE){
            doConnectCloseHandler(event);
        }else{
            throw new IllegalEventException("非法事件");
        }
    }

    private void doConnectOpenHandler(ConnectionEvent event){
        if(event.getState() == EVENT_STATE.SUCCESS){
            connOpenSuccessCount.incrementAndGet();
        }else{
            connOpenFailureCount.incrementAndGet();
            Driver driver = (Driver)event.getSource();
            if(connOpenFailInfo.size() < 16) {
                connOpenFailInfo.put(driver.toString(), event.getErrorMsg());
            }
        }
    }

    private void doConnectCloseHandler(ConnectionEvent event){
        if(event.getState() == EVENT_STATE.SUCCESS){
            connCloseSuccessCount.incrementAndGet();
        }else{
            connCloseFailureCount.incrementAndGet();
            Driver driver = (Driver)event.getSource();
            if(connCloseFailInfo.size() < 16) {
                connCloseFailInfo.put(driver.toString(), event.getErrorMsg());
            }
        }
    }

    @Override
    public Map<STAT_CONN,Object> getStatistics(){
        Map map= new HashMap(16);
        map.put(STAT_CONN.CONN_OPEN_SUCCESS_COUNT,connOpenSuccessCount.get());
        map.put(STAT_CONN.CONN_OPEN_FAILURE_COUNT,connOpenFailureCount.get());
        map.put(STAT_CONN.CONN_CLOSE_SUCCESS_COUNT,connCloseSuccessCount.get());
        map.put(STAT_CONN.CONN_CLOSE_FAILURE_COUNT,connCloseFailureCount.get());
        map.put(STAT_CONN.CONN_OPEN_FAILURE_INFO,connOpenFailInfo);
        map.put(STAT_CONN.CONN_CLOSE_FAILURE_INFO,connCloseFailInfo);
        return null;
    }

    @Override
    public String getStatisticsInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(STAT_CONN.CONN_OPEN_SUCCESS_COUNT.getName()).append(":").append(connOpenSuccessCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_OPEN_FAILURE_COUNT.getName()).append(":").append(connOpenFailureCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOSE_SUCCESS_COUNT.getName()).append(":").append(connCloseSuccessCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOSE_FAILURE_COUNT.getName()).append(":").append(connCloseFailureCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_OPEN_FAILURE_INFO.getName()).append(":").append(connOpenFailInfo.entrySet().stream()
                .map(item->item.getKey()+":"+item.getValue()).collect(Collectors.toList())).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOSE_FAILURE_INFO.getName()).append(":").append(connCloseFailInfo.entrySet().stream()
                .map(item->item.getKey()+":"+item.getValue()).collect(Collectors.toList()));
        return stringBuilder.toString();
    }

    @Override
    public void clearStatistics() {
        synchronized (this){
            connOpenSuccessCount.set(0);
            connOpenFailureCount.set(0);
            connCloseSuccessCount.set(0);
            connCloseFailureCount.set(0);
            connOpenFailInfo.clear();
            connCloseFailInfo.clear();
        }
    }



}
