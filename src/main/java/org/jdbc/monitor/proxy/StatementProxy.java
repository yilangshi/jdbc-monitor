package org.jdbc.monitor.proxy;

import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.common.STAT;
import org.jdbc.monitor.common.STATEMENT_METHOD;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.EventSupport;
import org.jdbc.monitor.event.StatementEvent;
import org.jdbc.monitor.event.type.STATEMENT_EVENT_TYPE;
import org.jdbc.monitor.util.PropertyUtils;
import org.jdbc.monitor.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * sql语句代理类
 * @author: shi rui
 * @create: 2018-12-25 11:13
 */
public class StatementProxy<E extends Statement> extends EventSupport implements InvocationHandler {

    private E statement;

    private String sql;

    protected List<String> batchedSql;

    private Object[] args;

    private String[] forbidWords = PropertyUtils.getString("forbid.word","").split(Constant.REGULAR_SPLIT_WORD);

    public StatementProxy(E statement){
        this.statement = statement;
    }

    public StatementProxy(E statement,String sql){
        this.statement = statement;
        this.sql = sql;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        STATEMENT_METHOD statementMethod = STATEMENT_METHOD.getStatementMethod(method.getName());
        if(statementMethod != null){
            this.args = args;
            return doHandler(statementMethod, method, args);
        }
        return method.invoke(statement, args);
    }

    private Object doHandler(STATEMENT_METHOD statementMethod, Method method, Object[] args) throws Throwable{
        switch (statementMethod) {
            case EXECUTE_QUERY:
                return handleCommInvoke(method,args,STATEMENT_EVENT_TYPE.STATEMENT_EXECUTE_QUERY);
            case EXECUTE_UPDATE:
                return handleCommInvoke(method,args,STATEMENT_EVENT_TYPE.STATEMENT_EXECUTE_UPDATE);
            case EXECUTE_SQL:
                return handleCommInvoke(method,args,STATEMENT_EVENT_TYPE.STATEMENT_EXECUTE_SQL);
            case ADD_BATCH:
                handlerAddBatch(args);
            case EXECUTE_BATCH:
                return handleCommInvoke(method,args,STATEMENT_EVENT_TYPE.STATEMENT_EXECUTE_BATCH);
            case CLEAR_BATCH:
                handlerClearBatch();
            default:return method.invoke(statement, args);
        }
    }

    private Object handleCommInvoke(Method method, Object[] args, STATEMENT_EVENT_TYPE eventType) throws Throwable{
        if(args != null && args.length > 0){
            this.sql = (String)args[0];
        }
        //检查是否有禁用的sql
        if(eventType == STATEMENT_EVENT_TYPE.STATEMENT_EXECUTE_BATCH){
            for(String sql:batchedSql){
                checkForbit(sql);
            }
        }else{
            checkForbit(this.getSql());
        }
        long start = System.currentTimeMillis();
        EVENT_STATE eventState = EVENT_STATE.SUCCESS;
        String errorMsg = null;
        Object result = null;
        try{
            return result = method.invoke(statement,args);
        }catch (Exception e){
            eventState = EVENT_STATE.FAIL;
            errorMsg= e.getMessage();
            throw e.getCause();
        }finally {
            publishEvent(StatementEvent.build(this, result, eventType, start, System.currentTimeMillis(), eventState, errorMsg));
        }
    }


    private void checkForbit(String sql){
        if(forbidWords == null || forbidWords.length ==0){
            return;
        }
        for(String forbidWord:forbidWords){
            if(StringUtils.isEmpty(forbidWord)){
                continue;
            }
            if(Pattern.compile(forbidWord).matcher(sql).find()){
                String errorMsg = String.format("SQL中包含禁用词%s", forbidWord);
                //发送禁用报警
                publishEvent(StatementEvent.build(this, null,
                        STATEMENT_EVENT_TYPE.STATEMENT_EXECUTE_FORBIT, System.currentTimeMillis(),
                        System.currentTimeMillis(), EVENT_STATE.SUCCESS, errorMsg));
                throw new RuntimeException(String.format("[%s]中包含禁用词%s",sql,forbidWord));
            }
        }

    }

    private void handlerAddBatch(Object[] args){
        if(this.statement instanceof Statement && args != null && args.length >0){
            addBatch((String)args[0]);
        }
    }

    private void handlerClearBatch(){
        if(batchedSql != null && !batchedSql.isEmpty()){
            clearBatch();
        }
    }

    private void addBatch(String sql) {
        synchronized(this) {
            if (this.batchedSql == null) {
                this.batchedSql = new ArrayList();
            }
            if (sql != null) {
                this.batchedSql.add(sql);
            }
        }
    }

    private void clearBatch() {
        synchronized(this) {
            if (this.batchedSql != null) {
                this.batchedSql.clear();
            }
        }
    }


    public E getStatement() {
        return statement;
    }

    public String getSql() {
        return sql;
    }

    public List<String> getBatchedSql() {
        return batchedSql;
    }

    public Object[] getArgs() {
        return args;
    }
}
