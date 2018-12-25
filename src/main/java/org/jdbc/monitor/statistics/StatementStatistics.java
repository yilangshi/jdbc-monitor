package org.jdbc.monitor.statistics;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.MonitorDriver;
import org.jdbc.monitor.common.*;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.StatementEvent;
import org.jdbc.monitor.event.type.STATEMENT_EVENT_TYPE;
import org.jdbc.monitor.listener.AbstractStatementMonitorEventListener;
import org.jdbc.monitor.proxy.StatementProxy;
import org.jdbc.monitor.util.Assert;
import org.jdbc.monitor.util.DateUtils;
import org.jdbc.monitor.util.LRUList;
import org.jdbc.monitor.util.SqlUtils;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * sql语句统计
 * @author: shi rui
 * @create: 2018-12-25 10:37
 */
@Slf4j
public class StatementStatistics extends AbstractStatementMonitorEventListener implements Statistics<STAT_STATEMENT> {

    /** sql统计，key为sql语句 **/
    private Map<String,SqlStatistics> sqlStatisticsMap = new ConcurrentHashMap<>(16);


    @Override
    public void onMonitorEvent(StatementEvent event) {
        log.debug("receive event:"+event.toString());
        switch (event.getEventType()){
            case STATEMENT_EXECUTE_QUERY:
                executeSqlHandler(event);
                break;
            case STATEMENT_EXECUTE_UPDATE:
                executeSqlHandler(event);
                break;
            case STATEMENT_EXECUTE_SQL:
                executeSqlHandler(event);
                break;
            case STATEMENT_EXECUTE_BATCH:
                executeBatchSqlHandler(event);
                break;
            default:
                //抛弃不处理的事件
        }
    }

    /**
     * 单个sql更新处理
     * @param event
     */
    private void executeSqlHandler(StatementEvent event){
        StatementProxy statementProxy = (StatementProxy)event.getSource();
        String sql = statementProxy.getSql();
        Assert.notNull(sql,String.format("[%s]事件错误,sql语句为空",event.getEventType()));
        synchronized (sqlStatisticsMap){
            int queryCount = 0;
            int updateCount = 0;
            if(SqlUtils.isSelect(sql)){
                if(event.getResult() instanceof ResultSet) {
                    ResultSet resultSet = (ResultSet) event.getResult();
                    queryCount = SqlUtils.getQueryCount(resultSet);
                }
            }else{
                updateCount = getUpdateCount(event);
            }
            doExecuteSqlHandlerEvent(event,sql,queryCount,updateCount);
        }
    }

    /**
     * 批量更新处理
     * @param event
     */
    private void executeBatchSqlHandler(StatementEvent event){
        StatementProxy statementProxy = (StatementProxy)event.getSource();
        List<String> sqlList = statementProxy.getBatchedSql();
        Assert.notNull(sqlList,String.format("[%s]事件错误,sql语句为空",event.getEventType()));
        synchronized (sqlStatisticsMap){
            for(int i = 0; i<sqlList.size(); i++) {
                String sql = sqlList.get(i);
                int updateCount = 0;
                if(event.getResult() != null){
                    updateCount = ((int[])event.getResult())[i];
                }
                doExecuteSqlHandlerEvent(event, sql, 0, updateCount);
            }
        }
    }

    private void doExecuteSqlHandlerEvent(StatementEvent event,String sql,int queryCount,int updateCount){
        SqlStatistics sqlStatistics = sqlStatisticsMap.get(sql);
        if(sqlStatistics == null){
            sqlStatistics = new SqlStatistics(sql);
        }
        //事务执行
        StatementProxy statement = (StatementProxy)event.getSource();
        //执行成功和失败次数
        sqlStatistics.getExecuteCount().incrementAndGet();
        if(event.getState() == EVENT_STATE.FAIL) {
            //失败次数
            sqlStatistics.getExecuteFailCount().incrementAndGet();
            //失败详情
            sqlStatistics.getFailInfo().add(getErrorInfo(statement, event));
        }else{
            if(!isAutoCommit(statement.getStatement())){
                sqlStatistics.getExecuteTranCount().incrementAndGet();
            }
            //计算平均时间
            long curAllTime = sqlStatistics.getExecuteAllTime().get();
            long usedTime = event.getFireTime() - event.getGenerateTime();
            sqlStatistics.getExecuteAllTime().compareAndSet(curAllTime, curAllTime + usedTime);
            sqlStatistics.setExecuteAvgTime(sqlStatistics.getExecuteAllTime().get()/sqlStatistics.getExecuteCount().get());
            //计算最大时间
            if(sqlStatistics.getExecuteMaxTime() < usedTime){
                sqlStatistics.setExecuteMaxTime(usedTime);
            }
            //计算执行时间分布
            STAT_TIME_DISTRIBUTION distribution = STAT_TIME_DISTRIBUTION.valueOf(usedTime);
            if(distribution != null){
                if(sqlStatistics.getExecuteTimeRange().get(distribution) != null){
                    sqlStatistics.getExecuteTimeRange().get(distribution).incrementAndGet();
                }else{
                    AtomicInteger atomicInteger = new AtomicInteger();
                    atomicInteger.incrementAndGet();
                    sqlStatistics.getExecuteTimeRange().put(distribution,atomicInteger);
                }
            }
            if(queryCount > 0){
                //读取行数
                long curCount = sqlStatistics.getReadCount().get();
                sqlStatistics.getReadCount().compareAndSet(curCount,curCount + queryCount);
            }
            if(updateCount > 0){
                //更新行数
                long curCount = sqlStatistics.getUpdateCount().get();
                sqlStatistics.getUpdateCount().compareAndSet(curCount,curCount + updateCount);
            }
            //最大并发 @TODO

        }
        sqlStatisticsMap.put(sql,sqlStatistics);
    }

    private int getQueryCount(StatementEvent event){
        int row = 0;
        try{
            if(event.getResult() instanceof ResultSet){
                ResultSet resultSet = (ResultSet)event.getResult();
                resultSet.last();
                row = resultSet.getRow();
                resultSet.beforeFirst();
            }
        }catch (Exception e){
            log.error("统计查询条数异常:",e);
        }
        return row;
    }


    private int getUpdateCount(StatementEvent event){
        Integer count = 0;
        if(event.getResult() instanceof Integer){
            count = (Integer)event.getResult();
        }
        return count;
    }

    private boolean isAutoCommit(Statement statement){
        try {
            return statement.getConnection().getAutoCommit();
        } catch (SQLException e) {
            log.error("判断是否是自动提交出错:",e);
            return false;
        }
    }

    /**
     * 获取失败信息，格式：【异常发生时间】【耗时】【sql】【arg】【error msg】
     * @param statementProxy
     * @param event
     * @return
     */
    private String getErrorInfo(StatementProxy statementProxy, StatementEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(DateUtils.format(event.getFireTime())).append("]");
        stringBuilder.append("[").append(event.getFireTime() - event.getGenerateTime()).append("ms]");
        stringBuilder.append("[").append(statementProxy.getSql()).append("]");
        stringBuilder.append(Arrays.toString(statementProxy.getArgs()));
        stringBuilder.append("[").append(event.getErrorMsg()).append("]");
        return stringBuilder.toString();
    }

    @Override
    public Map<STAT_STATEMENT, Object> getStatistics() {
        Map<STAT_STATEMENT,Object> map=  new TreeMap<>();
        //放入所有的sql统计详情
        List<SqlStatistics> statistics = new ArrayList<>(sqlStatisticsMap.values());
        map.put(STAT_STATEMENT.SQL_LIST,statistics);
        return map;
    }

    @Override
    public String getStatisticsInfo() {
        return sqlStatisticsMap.entrySet().stream().map(item -> item.getKey()+":\r\n"+item.getValue().getStatisticsInfo())
                .collect(Collectors.joining(";"));
    }

    public List<SqlStatistics> getSlowSqlTop10(){
        List<SqlStatistics> statistics = new ArrayList<>(sqlStatisticsMap.values());
        //统计慢sql
        Collections.sort(statistics);
        return statistics.subList(0,statistics.size()>=10?10:statistics.size());
    }

    @Override
    public void clearStatistics() {
        sqlStatisticsMap.clear();
    }

}
