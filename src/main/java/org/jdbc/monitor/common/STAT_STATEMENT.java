package org.jdbc.monitor.common;

/**
 * @author: shi rui
 * @create: 2018-12-25 10:38
 */
public enum  STAT_STATEMENT implements STAT {

    SQL_DETAIL("SQL"),
    SQL_EXECUTE_COUNT("执行次数"),
    SQL_EXECUTE_TRANSACTION_COUNT("事务执行次数"),
    SQL_EXECUTE_FAIL_COUNT("执行失败次数"),
    SQL_EXECUTE_AVG_TIME("平均耗时"),
    SQL_EXECUTE_MAX_TIME("最大耗时"),
    SQL_EXECUTE_TIME_RANGE("执行时间分布"),
    SQL_READ_COUNT("读取行数"),
    SQL_UPDATE_COUNT("更新行数"),
    SQL_CONCURRENCY_COUNT("最大并发"),
    SQL_FAILURE_INFO("失败详情"),
    SQL_SLOW_TOP10("慢SQL-Top10"),
    SQL_LIST("统计集合"),
    ;

    private String name;

    STAT_STATEMENT(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
