package org.jdbc.monitor.warn;

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
}
