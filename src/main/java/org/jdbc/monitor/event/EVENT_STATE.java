package org.jdbc.monitor.event;

/**
 * @author: shi rui
 * @create: 2018-12-17 15:40
 */
public enum EVENT_STATE {
    /** 状态 */
    SUCCESS(0,"成功"),
    FAIL(1,"异常");

    private int code;

    private String name;


    EVENT_STATE(int code,String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
