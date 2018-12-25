package org.jdbc.monitor.event.type;

/**
 * @author: shi rui
 * @create: 2018-12-25 11:00
 */
public enum STATEMENT_EVENT_TYPE implements EventType {
    STATEMENT_EXECUTE_QUERY,
    STATEMENT_EXECUTE_UPDATE,
    STATEMENT_EXECUTE_SQL,
    STATEMENT_EXECUTE_BATCH,
    /** sql禁用报警 */
    STATEMENT_EXECUTE_FORBIT
    ;


    @Override
    public String getName() {
        return this.toString();
    }
}
