package org.jdbc.monitor.listener;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:50
 */
public interface WarnListener<E extends Warn> {

    void addWarn(E warn);

    void alert(String msg);

    /**
     * 获取报警名称
     * @return
     */
    String getWarnName();

    /**
     * 获取报警次数
     * @return
     */
    int getWarnTime();
}
