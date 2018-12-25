package org.jdbc.monitor.common;

/**
 * @author: shi rui
 * @create: 2018-12-25 14:01
 */
public enum  STATEMENT_METHOD {
    EXECUTE_QUERY("executeQuery","执行查询方法"),
    EXECUTE_UPDATE("executeUpdate","执行更新方法"),
    EXECUTE_SQL("execute","执行SQL方法"),
    ADD_BATCH("addBatch","增加批量Sql"),
    EXECUTE_BATCH("executeBatch","执行批量Sql"),
    CLEAR_BATCH("clearBatch","清除批量Sql")
    ;

    private String code;

    private String name;

    STATEMENT_METHOD(String code,String name){
        this.code = code;
        this.name = name;
    }

    public static STATEMENT_METHOD getStatementMethod(String code){
        STATEMENT_METHOD[] values = STATEMENT_METHOD.values();
        for(STATEMENT_METHOD method:values){
            if(method.getCode().equals(code)){
                return method;
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
