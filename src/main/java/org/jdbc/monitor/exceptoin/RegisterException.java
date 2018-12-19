package org.jdbc.monitor.exceptoin;

/**
 * @author: shi rui
 * @create: 2018-12-12 15:11
 */
public class RegisterException extends RuntimeException {

    public RegisterException(String message){
        super(message);
    }

    public RegisterException(String message, Throwable cause){
        super(message, cause);
    }



}
