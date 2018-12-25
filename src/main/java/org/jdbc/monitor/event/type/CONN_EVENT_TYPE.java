package org.jdbc.monitor.event.type;

/**
 * 数据库连接事件
 * @author: shi rui
 * @create: 2018-12-14 18:06
 */
public enum CONN_EVENT_TYPE implements EventType{
    CONN_OPEN,
    CONN_CLOSE,
    CONN_CREATE_STATEMENT,
    CONN_CREATE_PREPARED_STATEMENT,
    CONN_CREATE_CALLABLE_STATEMENT,
    CONN_CREATE_CLOB,
    CONN_CREATE_BLOB,
    CONN_COMMIT,
    CONN_ROLLBACK
    ;


    @Override
    public String getName() {
        return this.toString();
    }
}
