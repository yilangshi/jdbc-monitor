package org.jdbc.monitor.event.type;

/**
 * 数据库连接事件
 * @author: shi rui
 * @create: 2018-12-14 18:06
 */
public enum CONN_EVENT implements EventType{
    CONN_OPEN,
    CONN_CLOSE
    ;


    @Override
    public String getName() {
        return this.toString();
    }
}
