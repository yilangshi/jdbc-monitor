package org.jdbc.monitor.common;

/**
 * 概要统计
 * @author: shi rui
 * @create: 2018-12-20 11:27
 */
public enum STAT_PROFILE implements STAT {
    PROFILE_DRIVER_NAME("驱动"),
    PROFILE_DRIVER_VERSION("驱动版本"),
    PROFILE_JAVA_VERSION("JAVA版本"),
    PROFILE_JVM_NAME("JVM名称"),
    PROFILE_CLASS_PATH("类路径"),
    PROFILE_DB_URL("数据库链接串"),
    PROFILE_DB_USER("数据用户名"),
    PROFILE_START_TIME("应用启动时间"),
    PROFILE_RESET_FLAG("是否允许重置"),
    PROFILE_RESET_TIME("重置次数")
    ;

    private String name;

    STAT_PROFILE(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
