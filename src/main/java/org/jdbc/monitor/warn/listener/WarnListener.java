package org.jdbc.monitor.warn.listener;

import org.jdbc.monitor.warn.Warn;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:50
 */
public interface WarnListener<E extends Warn> {

    void addWarn(E warn);

    void alert(String msg);
}
