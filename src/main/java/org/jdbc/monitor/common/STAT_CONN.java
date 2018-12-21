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
    CONN_CLOSE_FAILURE_INFO("关闭失败信息")
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
