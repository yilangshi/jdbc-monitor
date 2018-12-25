package org.jdbc.monitor.common;

/**
 * @author: shi rui
 * @create: 2018-12-17 18:23
 */
public enum CONN_METHOD {

    CLOSE("close","关闭"),
    CREATE_STATEMENT("createStatement","创建Statement"),
    CREATE_PREPARED_STATEMENT("prepareStatement","创建prepareStatement"),
    CREATE_CALLABLE_STATEMENT("prepareCall","创建prepareCall"),
    CREATE_CLOB("createClob","创建clob"),
    CREATE_BLOB("createBlob","创建blob"),
    COMMIT("commit","提交"),
    ROLLBACK("rollback","回滚"),

    ;

    private String code;

    private String name;

    CONN_METHOD(String code,String name){
        this.code = code;
        this.name = name;
    }

    public static CONN_METHOD getConnMethod(String code){
        CONN_METHOD[] values = CONN_METHOD.values();
        for(CONN_METHOD connMethod:values){
            if(connMethod.getCode().equals(code)){
                return connMethod;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return name;
    }
}
