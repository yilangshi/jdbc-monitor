package org.jdbc.monitor.common;

/**
 * 报警统计
 * @author: shi rui
 * @create: 2018-12-20 11:27
 */
public enum STAT_WARN implements STAT {
    /** 统计信息 */
    WARN_ALL_COUNT("报警总次数"),
    WARN_CONNECT_OPEN("连接打开失败报警次数"),
    WARN_CONNECT_CLOSE("连接关闭失败报警次数"),
    WARN_EXECUTE_SQL("执行SQL失败报警次数"),
    WARN_EXECUTE_TIMEOUT("执行超时报警次数"),
    WARN_READ_OVER_COUNT("查询记录数过大报警次数"),
    WARN_UPDATE_OVER_COUNT("更新记录数过大报警次数"),
    WARN_EXECUTE_FORBIT_COUNT("执行禁用词报警次数"),
    WARN_EXECUTE_WARNWORD_COUNT("执行敏感词报警次数"),
    WARN_EMAILS("接受报警EMAIL")
    ;

    private String name;

    STAT_WARN(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
