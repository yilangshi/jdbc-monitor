package org.jdbc.monitor.exceptoin;

/**
 * @author: shi rui
 * @create: 2018-12-14 15:59
 */
public class SubmitEventException extends RuntimeException {

    public SubmitEventException(String message, Throwable cause){
        super(message, cause);
    }
}
