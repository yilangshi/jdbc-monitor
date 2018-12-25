package org.jdbc.monitor.listener;

/**
 * 报警接口
 * @author: shi rui
 * @create: 2018-12-18 11:07
 */
public interface Warn {
    /**
     * 推送报警
     * @param msg
     */
    void alert(String msg);

    /**
     * 获取报警的描述头,建议格式：[appName][warnType]
     * @return
     */
    String getWarnHeader();
}
