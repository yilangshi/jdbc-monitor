package org.jdbc.monitor.common;

/**
 * @author: shi rui
 * @create: 2018-12-17 18:23
 */
public enum CONN_METHOD {

    CLOSE("close","关闭");

    private String code;

    private String name;

    CONN_METHOD(String code,String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return name;
    }
}
