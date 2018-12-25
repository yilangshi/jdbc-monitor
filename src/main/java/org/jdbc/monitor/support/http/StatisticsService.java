package org.jdbc.monitor.support.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jdbc.monitor.Configuration;
import org.jdbc.monitor.common.*;
import org.jdbc.monitor.statistics.SqlStatistics;
import org.jdbc.monitor.util.PropertyUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: shi rui
 * @create: 2019-01-22 14:41
 */
public class StatisticsService {

    private Configuration configuration = Configuration.getInstance();

    /** 概要统计信息 */
    private static final String STATISTICS_SUMMARY = "statistics_summary.json";

    /** 连接统计 */
    private static final String STATISTICS_CONNECT = "statistics_connect.json";

    /** 执行sql统计 */
    private static final String STATISTICS_STATEMENT = "statistics_statement.json";

    /** 慢sql top10统计 */
    private static final String STATISTICS_SLOW_SQL = "statistics_slow_sql.json";

    /** 报警统计 */
    private static final String STATISTICS_WARN = "statistics_warn.json";

    public String service(String url){
        if(url.endsWith(STATISTICS_SUMMARY)){
           return getProfileStatistics();
        }else if(url.endsWith(STATISTICS_CONNECT)){
            return getConnectStatistics();
        }else if(url.endsWith(STATISTICS_STATEMENT)){
            return getStatementStatistics();
        }else if(url.endsWith(STATISTICS_SLOW_SQL)){
            return getSlowStatementStatistics();
        }else if(url.endsWith(STATISTICS_WARN)){
            return getWarnStatistics();
        }
        return "";
    }

    private String getProfileStatistics(){
        Map<STAT_PROFILE, Object> statistics = configuration.getProfileStatistics().getStatistics();
        return JSON.toJSONString(doStatisticsInfoKey(statistics));
    }

    private String getConnectStatistics(){
        Map<STAT_CONN, Object> statistics = configuration.getConnectionStatistics().getStatistics();
        return JSON.toJSONString(doStatisticsInfoKey(statistics));
    }

    private String getStatementStatistics(){
        Object statisticsObject = configuration.getStatementStatistics().getStatistics().get(STAT_STATEMENT.SQL_LIST);
        List<SqlStatistics> sqlStatistics = (List<SqlStatistics>)statisticsObject;
        List<Map<STAT_STATEMENT,Object>> statisticsList = new LinkedList<>();
        for(SqlStatistics statistics:sqlStatistics){
            statisticsList.add(statistics.getStatistics());
        }
        return JSON.toJSONString(statisticsList);
    }

    private String getSlowStatementStatistics(){
        List<SqlStatistics> sqlStatistics = configuration.getStatementStatistics().getSlowSqlTop10();
        List<Map<STAT_STATEMENT,Object>> statisticsList = new LinkedList<>();
        for(SqlStatistics statistics:sqlStatistics){
            statisticsList.add(statistics.getStatistics());
        }
        return JSON.toJSONString(statisticsList);
    }

    private <T extends STAT> List<JSONObject> doStatisticsInfoKey(Map<T, Object> statistics){
        List<JSONObject> newStatistics = new LinkedList<>();
        for(Map.Entry<T, Object> entry: statistics.entrySet()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",entry.getKey().getName());
            jsonObject.put("value",entry.getValue());
            newStatistics.add(jsonObject);
        }
        return newStatistics;
    }

    private String getWarnStatistics(){
        Map<STAT_WARN,Object> warnStatistics = new TreeMap<>();
        warnStatistics.put(STAT_WARN.WARN_ALL_COUNT,0);
        warnStatistics.put(STAT_WARN.WARN_CONNECT_OPEN,0);
        warnStatistics.put(STAT_WARN.WARN_CONNECT_CLOSE,0);
        warnStatistics.put(STAT_WARN.WARN_EXECUTE_SQL,0);
        warnStatistics.put(STAT_WARN.WARN_EXECUTE_TIMEOUT,0);
        warnStatistics.put(STAT_WARN.WARN_READ_OVER_COUNT,0);
        warnStatistics.put(STAT_WARN.WARN_UPDATE_OVER_COUNT,0);
        warnStatistics.put(STAT_WARN.WARN_EXECUTE_FORBIT_COUNT,0);
        warnStatistics.put(STAT_WARN.WARN_EXECUTE_WARNWORD_COUNT,0);
        warnStatistics.put(STAT_WARN.WARN_EMAILS, PropertyUtils.getString("warn.email.to.account",""));
        return JSON.toJSONString(doStatisticsInfoKey(warnStatistics));
    }
}
