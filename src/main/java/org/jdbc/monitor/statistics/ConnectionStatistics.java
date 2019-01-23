package org.jdbc.monitor.statistics;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.MonitorDriver;
import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.common.STAT_CONN;
import org.jdbc.monitor.common.STAT_TIME_DISTRIBUTION;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.listener.AbstractConnectionMonitorEventListener;
import org.jdbc.monitor.proxy.ConnectionProxy;
import org.jdbc.monitor.util.DateUtils;
import org.jdbc.monitor.util.LRUList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author: shi rui
 * @create: 2018-12-17 15:59
 */
@Slf4j
public class ConnectionStatistics extends AbstractConnectionMonitorEventListener implements Statistics<STAT_CONN>{

    /** 打开成功数 */
    private final AtomicLong connOpenSuccessCount = new AtomicLong(0);

    private final AtomicLong connOpenFailureCount = new AtomicLong(0);

    private final AtomicLong connCloseSuccessCount = new AtomicLong(0);

    private final AtomicLong connCloseFailureCount = new AtomicLong(0);

    private final LRUList<String> connOpenFailInfo = new LRUList<>(Constant.LIST_MAX_SIZE);

    private final LRUList<String> connCloseFailInfo = new LRUList<>(Constant.LIST_MAX_SIZE);

    /** 链接打开超时时间 */
    private volatile long connOpenTimeout;

    /** 链接存活时间 */
    private volatile long connWaitTimeout;

    /** 生存时间分布，按5段时间范围统计:
     * 0-1ms次数,1-10ms次数,10-100ms次数,100-1000ms次数,
     * 1-10s次数,10-100s次数,100-1000s次数,大于1000s次数 */
    private final Map<STAT_TIME_DISTRIBUTION,AtomicLong> connKeepaliveTimeRange =  Collections.synchronizedMap(new TreeMap<>());

    /** 最大并发数 */
    private volatile int concurrenceCount;

    /** statement打开次数 */
    private final AtomicLong statementCount = new AtomicLong(0);

    /** prepared statement打开次数 */
    private final AtomicLong preparedStatementCount = new AtomicLong(0);

    /** callable statement打开次数 */
    private final AtomicLong callableStatementCount = new AtomicLong(0);

    /** callable statement打开次数 */
    private final AtomicLong commitCount = new AtomicLong(0);

    /** callable statement打开次数 */
    private final AtomicLong rollbackCount = new AtomicLong(0);

    /** clob 打开次数 */
    private final AtomicLong clobCount = new AtomicLong(0);
    /** blob 打开次数 */
    private final AtomicLong blobCount = new AtomicLong(0);

    public ConnectionStatistics(){
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1MS,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_10MS,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_100MS,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1000MS,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_10S,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_100S,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1000S,new AtomicLong(0));
        connKeepaliveTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1000S_BG,new AtomicLong(0));
    }

    @Override
    public void onMonitorEvent(ConnectionEvent event) {
        log.debug("receive event:"+event.toString());
        switch (event.getEventType()){
            case CONN_OPEN:
                doConnectOpenHandler(event);
                break;
            case CONN_CLOSE:
                doConnectCloseHandler(event);
                break;
            case CONN_CREATE_STATEMENT:
                statementCount.incrementAndGet();
                break;
            case CONN_CREATE_PREPARED_STATEMENT:
                preparedStatementCount.incrementAndGet();
                break;
            case CONN_CREATE_CALLABLE_STATEMENT:
                callableStatementCount.incrementAndGet();
                break;
            case CONN_CREATE_CLOB:
                clobCount.incrementAndGet();
                break;
            case CONN_CREATE_BLOB:
                blobCount.incrementAndGet();
                break;
            case CONN_COMMIT:
                commitCount.incrementAndGet();
                break;
            case CONN_ROLLBACK:
                rollbackCount.incrementAndGet();
                break;
            default:
                //抛弃不处理的事件
        }
    }


    private void doConnectOpenHandler(ConnectionEvent event){
        if(event.getState() == EVENT_STATE.SUCCESS){
            connOpenSuccessCount.incrementAndGet();
        }else{
            connOpenFailureCount.incrementAndGet();
            MonitorDriver driver = (MonitorDriver)event.getSource();
            connOpenFailInfo.add(getErrorInfo(driver,event));
            //判断是否是超时异常（java.net.ConnectException: Connection timed out），如果是设置超时时间
            if(Constant.CONN_OPEN_TIME_OUT_MSG.equals(event.getErrorMsg())){
                connOpenTimeout = event.getFireTime() - event.getGenerateTime();
            }

        }
    }

    private void doConnectCloseHandler(ConnectionEvent event){
        if(event.getState() == EVENT_STATE.SUCCESS){
            connCloseSuccessCount.incrementAndGet();
            //计算链接生存时间
            ConnectionProxy connectionProxy = (ConnectionProxy)event.getSource();
            addTimeRange(event.getFireTime() - connectionProxy.getStartTime());
        }else{
            connCloseFailureCount.incrementAndGet();
            MonitorDriver driver = (MonitorDriver)event.getSource();
            connCloseFailInfo.add(getErrorInfo(driver,event));
        }
    }

    private void addTimeRange(long time){
        synchronized (connKeepaliveTimeRange) {
            STAT_TIME_DISTRIBUTION distribution = STAT_TIME_DISTRIBUTION.valueOf(time);
            if(connKeepaliveTimeRange.get(distribution) != null){
                connKeepaliveTimeRange.get(distribution).incrementAndGet();
            }else {
                AtomicLong range = new AtomicLong();
                range.incrementAndGet();
                connKeepaliveTimeRange.put(distribution, range);
            }
        }
    }

    /**
     * 获取失败信息，格式：【异常发生时间】【耗时】【dburl】【dbuser】【error msg】
     * @param driver
     * @param event
     * @return
     */
    private String getErrorInfo(MonitorDriver driver,ConnectionEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(DateUtils.format(event.getFireTime())).append("]");
        stringBuilder.append("[").append(event.getFireTime() - event.getGenerateTime()).append("ms]");
        stringBuilder.append("[").append(driver.getDbUrl()).append("]");
        stringBuilder.append("[").append(driver.getDbUser()).append("]");
        stringBuilder.append("[").append(event.getErrorMsg()).append("]");
        return stringBuilder.toString();
    }

    @Override
    public Map<STAT_CONN,Object> getStatistics(){
        Map<STAT_CONN,Object> map=  new TreeMap<>();
        map.put(STAT_CONN.CONN_OPEN_SUCCESS_COUNT,connOpenSuccessCount.get());
        map.put(STAT_CONN.CONN_OPEN_FAILURE_COUNT,connOpenFailureCount.get());
        map.put(STAT_CONN.CONN_CLOSE_SUCCESS_COUNT,connCloseSuccessCount.get());
        map.put(STAT_CONN.CONN_CLOSE_FAILURE_COUNT,connCloseFailureCount.get());
        map.put(STAT_CONN.CONN_OPEN_FAILURE_INFO,connOpenFailInfo);
        map.put(STAT_CONN.CONN_CLOSE_FAILURE_INFO,connCloseFailInfo);
        map.put(STAT_CONN.CONN_OPEN_TIMEOUT,connOpenTimeout);
        map.put(STAT_CONN.CONN_KEEPALIVE_TIME,connWaitTimeout);
        map.put(STAT_CONN.CONN_KEEPALIVE_TIME_RANGE,getKeepAliveTimeRange());
        map.put(STAT_CONN.CONN_CONCURRENCY_COUNT,concurrenceCount);
        map.put(STAT_CONN.CONN_STATEMENT_COUNT,statementCount);
        map.put(STAT_CONN.CONN_PREPARED_STATEMENT_COUNT,preparedStatementCount);
        map.put(STAT_CONN.CONN_CALLABLE_STATEMENT_COUNT,callableStatementCount);
        map.put(STAT_CONN.CONN_CLOB_COUNT,clobCount);
        map.put(STAT_CONN.CONN_BLOB_COUNT,blobCount);
        map.put(STAT_CONN.CONN_COMMIT_COUNT,commitCount);
        map.put(STAT_CONN.CONN_ROLLBACK_COUNT,rollbackCount);
        return map;
    }

    private String getKeepAliveTimeRange(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1MS).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_10MS).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_100MS).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1000MS).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_10S).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1MS).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_100S).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1000S).get()).append(",");
        stringBuilder.append(connKeepaliveTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1000S_BG).get());
        return stringBuilder.toString();
    }

    @Override
    public String getStatisticsInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(STAT_CONN.CONN_OPEN_SUCCESS_COUNT.getName()).append(":").append(connOpenSuccessCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_OPEN_FAILURE_COUNT.getName()).append(":").append(connOpenFailureCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOSE_SUCCESS_COUNT.getName()).append(":").append(connCloseSuccessCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOSE_FAILURE_COUNT.getName()).append(":").append(connCloseFailureCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_OPEN_FAILURE_INFO.getName()).append(":").append(connOpenFailInfo.stream().collect(Collectors.toList())).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOSE_FAILURE_INFO.getName()).append(":").append(connCloseFailInfo.stream().collect(Collectors.toList())).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_OPEN_TIMEOUT.getName()).append(":").append(connOpenTimeout).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_KEEPALIVE_TIME.getName()).append(":").append(connWaitTimeout).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_KEEPALIVE_TIME_RANGE.getName()).append(":").append(getRangeStr()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CONCURRENCY_COUNT.getName()).append(":").append(concurrenceCount).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_STATEMENT_COUNT.getName()).append(":").append(statementCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_PREPARED_STATEMENT_COUNT.getName()).append(":").append(preparedStatementCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CALLABLE_STATEMENT_COUNT.getName()).append(":").append(callableStatementCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_CLOB_COUNT.getName()).append(":").append(clobCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_BLOB_COUNT.getName()).append(":").append(blobCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_COMMIT_COUNT.getName()).append(":").append(commitCount.get()).append("\r\n");
        stringBuilder.append(STAT_CONN.CONN_ROLLBACK_COUNT.getName()).append(":").append(rollbackCount.get()).append("\r\n");
        return stringBuilder.toString();
    }

    private String getRangeStr(){
        return connKeepaliveTimeRange.entrySet().stream().map(item->item.getKey().getName()+":"+item.getValue()).collect(Collectors.joining(","));
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
            connOpenTimeout = 0;
            connKeepaliveTimeRange.clear();
            concurrenceCount = 0;
            statementCount.set(0);
            preparedStatementCount.set(0);
            callableStatementCount.set(0);
            clobCount.set(0);
            blobCount.set(0);
            commitCount.set(0);
            rollbackCount.set(0);
        }
    }



}
