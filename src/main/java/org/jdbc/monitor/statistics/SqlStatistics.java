package org.jdbc.monitor.statistics;

import com.alibaba.fastjson.annotation.JSONField;
import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.common.STAT_STATEMENT;
import org.jdbc.monitor.common.STAT_TIME_DISTRIBUTION;
import org.jdbc.monitor.util.LRUList;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author: shi rui
 * @create: 2019-01-22 18:42
 */
public class SqlStatistics implements Statistics<STAT_STATEMENT>, Comparable<SqlStatistics> {
    private final String sql;
    private final AtomicLong executeCount = new AtomicLong(0);
    private final AtomicLong executeFailCount = new AtomicLong(0);
    private final AtomicLong executeTranCount = new AtomicLong(0);
    private AtomicLong executeAllTime = new AtomicLong(0);
    private long executeAvgTime;
    private long executeMaxTime;
    private final Map<STAT_TIME_DISTRIBUTION,AtomicInteger> executeTimeRange = new ConcurrentHashMap<>();
    private final AtomicLong readCount = new AtomicLong(0);
    private final AtomicLong updateCount = new AtomicLong(0);
    private int concurrenceCount;
    private final LRUList<String> failInfo = new LRUList<>(Constant.LIST_MAX_SIZE);

    public SqlStatistics(String sql){
        this.sql = sql;
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1MS,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_10MS,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_100MS,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1000MS,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_10S,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_100S,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1000S,new AtomicInteger(0));
        executeTimeRange.put(STAT_TIME_DISTRIBUTION.TIME_1000S_BG,new AtomicInteger(0));
    }

    @JSONField(serialize=false)
    @Override
    public Map<STAT_STATEMENT, Object> getStatistics() {
        Map<STAT_STATEMENT,Object> map=  new TreeMap<>();
        map.put(STAT_STATEMENT.SQL_DETAIL,sql);
        map.put(STAT_STATEMENT.SQL_EXECUTE_COUNT, executeCount);
        map.put(STAT_STATEMENT.SQL_EXECUTE_TRANSACTION_COUNT, executeTranCount);
        map.put(STAT_STATEMENT.SQL_EXECUTE_FAIL_COUNT, executeFailCount);
        map.put(STAT_STATEMENT.SQL_EXECUTE_AVG_TIME, executeAvgTime);
        map.put(STAT_STATEMENT.SQL_EXECUTE_MAX_TIME, executeMaxTime);
        map.put(STAT_STATEMENT.SQL_EXECUTE_TIME_RANGE, getTimeRange());
        map.put(STAT_STATEMENT.SQL_READ_COUNT, readCount);
        map.put(STAT_STATEMENT.SQL_UPDATE_COUNT, updateCount);
        map.put(STAT_STATEMENT.SQL_CONCURRENCY_COUNT, concurrenceCount);
        map.put(STAT_STATEMENT.SQL_FAILURE_INFO, failInfo);
        return map;
    }



    @JSONField(serialize=false)
    @Override
    public String getStatisticsInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(STAT_STATEMENT.SQL_DETAIL.getName()).append(":").append(sql).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_EXECUTE_COUNT.getName()).append(":").append(executeCount.get()).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_EXECUTE_TRANSACTION_COUNT.getName()).append(":").append(executeTranCount.get()).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_EXECUTE_AVG_TIME.getName()).append(":").append(executeAvgTime).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_EXECUTE_MAX_TIME.getName()).append(":").append(executeMaxTime).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_EXECUTE_TIME_RANGE.getName()).append(":").append(getTimeRange()).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_READ_COUNT.getName()).append(":").append(readCount).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_UPDATE_COUNT.getName()).append(":").append(updateCount).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_CONCURRENCY_COUNT.getName()).append(":").append(concurrenceCount).append("\r\n");
        stringBuilder.append(STAT_STATEMENT.SQL_FAILURE_INFO.getName()).append(":").append(failInfo.stream().collect(Collectors.toList())).append("\r\n");
        return stringBuilder.toString();
    }

    private String getTimeRange(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1MS).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_10MS).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_100MS).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1000MS).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_10S).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1MS).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_100S).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1000S).get()).append(",");
        stringBuilder.append(executeTimeRange.get(STAT_TIME_DISTRIBUTION.TIME_1000S_BG).get());
        return stringBuilder.toString();
    }

    @Override
    public void clearStatistics() {

    }

    public String getSql() {
        return sql;
    }

    public AtomicLong getReadCount() {
        return readCount;
    }

    public AtomicLong getUpdateCount() {
        return updateCount;
    }

    public int getConcurrenceCount() {
        return concurrenceCount;
    }

    public LRUList<String> getFailInfo() {
        return failInfo;
    }

    public AtomicLong getExecuteCount() {
        return executeCount;
    }

    public AtomicLong getExecuteTranCount() {
        return executeTranCount;
    }

    public long getExecuteAvgTime() {
        return executeAvgTime;
    }

    public long getExecuteMaxTime() {
        return executeMaxTime;
    }

    public AtomicLong getExecuteFailCount() {
        return executeFailCount;
    }

    public AtomicLong getExecuteAllTime() {
        return executeAllTime;
    }

    public void setExecuteAvgTime(long executeAvgTime) {
        this.executeAvgTime = executeAvgTime;
    }

    public void setExecuteMaxTime(long executeMaxTime) {
        this.executeMaxTime = executeMaxTime;
    }

    public void setConcurrenceCount(int concurrenceCount) {
        this.concurrenceCount = concurrenceCount;
    }

    public Map<STAT_TIME_DISTRIBUTION, AtomicInteger> getExecuteTimeRange() {
        return executeTimeRange;
    }

    @Override
    public int compareTo(SqlStatistics sqlStatistics) {
        return Long.valueOf(executeAvgTime - sqlStatistics.getExecuteAvgTime()).intValue();
    }
}
