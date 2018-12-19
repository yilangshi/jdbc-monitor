package org.jdbc.monitor.exceptoin;

/**
 * @author: shi rui
 * @create: 2018-12-13 17:30
 */
public class TaskRejectedException extends RuntimeException {
    public TaskRejectedException(String msg) {
        super(msg);
    }
    public TaskRejectedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
