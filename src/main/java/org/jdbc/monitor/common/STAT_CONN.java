package org.jdbc.monitor.common;

/**
 * connection statistics info
 * @author: shi rui
 * @create: 2018-12-18 14:50
 */
public enum STAT_CONN implements STAT{
    CONN_OPEN_SUCCESS_COUNT("连接成功数"),
    CONN_OPEN_FAILURE_COUNT("连接失败数"),
    CONN_CLOSE_SUCCESS_COUNT("关闭成功数"),
    CONN_CLOSE_FAILURE_COUNT("关闭失败数"),
    CONN_OPEN_FAILURE_INFO("连接失败信息"),
    CONN_CLOSE_FAILURE_INFO("关闭失败信息"),
    CONN_OPEN_TIMEOUT("链接超时时间"),
    CONN_KEEPALIVE_TIME("链接生存时间"),
    CONN_KEEPALIVE_TIME_RANGE("链接生存时间分布"),
    CONN_CONCURRENCY_COUNT("并发连接数"),
    CONN_STATEMENT_COUNT("Statement打开次数"),
    CONN_PREPARED_STATEMENT_COUNT("PreparedStatement打开次数"),
    CONN_CALLABLE_STATEMENT_COUNT("CallableStatement打开次数"),
    CONN_COMMIT_COUNT("提交数"),
    CONN_ROLLBACK_COUNT("提交数")
    ;

    private String name;

    STAT_CONN(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
