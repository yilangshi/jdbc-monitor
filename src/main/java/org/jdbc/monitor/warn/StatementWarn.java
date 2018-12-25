package org.jdbc.monitor.warn;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.StatementEvent;
import org.jdbc.monitor.listener.AbstractStatementWarnListener;
import org.jdbc.monitor.listener.Warn;
import org.jdbc.monitor.proxy.StatementProxy;
import org.jdbc.monitor.util.DateUtils;
import org.jdbc.monitor.util.PropertyUtils;
import org.jdbc.monitor.util.SqlUtils;
import org.jdbc.monitor.util.StringUtils;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: shi rui
 * @create: 2019-01-17 15:13
 */
@Slf4j
public class StatementWarn<E extends Warn> extends AbstractStatementWarnListener<E> {

    private long timeout = PropertyUtils.getLong("timeout.alert.time");

    private int readMaxCount = PropertyUtils.getInt("max.count.read.alert");

    private int updateMaxCount = PropertyUtils.getInt("max.count.update.alert");

    private String[] warnWords = PropertyUtils.getString("warn.word","").split(Constant.REGULAR_SPLIT_WORD);

    public StatementWarn(List<E> warnList){
        super(warnList);
    }

    @Override
    public void onMonitorEvent(StatementEvent event) {
        log.debug("receive event:"+event.toString());
        switch (event.getEventType()){
            case STATEMENT_EXECUTE_QUERY:
                doSqlExecuteEventHandler(event);
                break;
            case STATEMENT_EXECUTE_UPDATE:
                doSqlExecuteEventHandler(event);
                break;
            case STATEMENT_EXECUTE_SQL:
                doSqlExecuteEventHandler(event);
                break;
            case STATEMENT_EXECUTE_BATCH:
                doBatchSqlExecuteEventHandler(event);
                break;
            case STATEMENT_EXECUTE_FORBIT:
                doSqlForbitEventHandler(event);
                break;
            default:
                //抛弃不处理的事件
        }
    }

    private void doSqlExecuteEventHandler(StatementEvent event){
        if(event.getState() == EVENT_STATE.FAIL){
            sendErrorWarn(event,event.getErrorMsg());
            return;
        }
        StatementProxy statementProxy = (StatementProxy)event.getSource();
        //超时报警
        if(isOverTime(event)){
            sendErrorWarn(event,"over time "+timeout+"ms");
        }
        //超过读取或更新阀值报警
        if(SqlUtils.isSelect(statementProxy.getSql())){
            if(event.getResult() instanceof ResultSet) {
                ResultSet resultSet = (ResultSet) event.getResult();
                int queryCount = SqlUtils.getQueryCount(resultSet);
                if(queryCount > readMaxCount){
                    sendErrorWarn(event,"read count over "+readMaxCount+".");
                }
            }
        }else{
            if(event.getResult() instanceof Integer){
                int updateCount = (Integer)event.getResult();
                if(updateCount > updateMaxCount){
                    sendErrorWarn(event,"update count over "+updateMaxCount+".");
                }
            }
        }
        //关键词报警
        if(warnWords != null && warnWords.length >0){
            for(String warnWord:warnWords){
                if(StringUtils.isEmpty(warnWord)){
                    continue;
                }
                if(Pattern.compile(warnWord).matcher(statementProxy.getSql()).find()){
                    String errorMsg = String.format("sql中包含关键词"+warnWord);
                    sendErrorWarn(event,errorMsg);
                    break;
                }
            }
        }

    }

    private void doBatchSqlExecuteEventHandler(StatementEvent event){
        if(event.getState() == EVENT_STATE.FAIL){
            sendBatchErrorWarn(event,event.getErrorMsg());
            return;
        }
        //超时报警
        if(isOverTime(event)){
            sendBatchErrorWarn(event,"over time "+timeout+"ms");
        }
        //超过更新阀值报警
        if(event.getResult() != null){
            int[] result = (int[])event.getResult();
            for(int i = 0; i < result.length; i++){
                int count = result[i];
                if(count > updateMaxCount){
                    sendBatchErrorWarn(event,"over time "+updateMaxCount+"ms");
                    break;
                }
            }
        }
        //关键词报警
        if(warnWords != null && warnWords.length >0){
            StatementProxy statementProxy = (StatementProxy)event.getSource();
            List<String> sqlList = statementProxy.getBatchedSql();
            for(String warnWord:warnWords){
                for(String sql:sqlList) {
                    if (Pattern.compile(warnWord).matcher(sql).find()) {
                        String errorMsg = String.format("sql中包含关键词" + warnWord);
                        sendErrorWarn(event, errorMsg);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 发送禁用词报警
     * @param event
     */
    private void doSqlForbitEventHandler(StatementEvent event){
        sendErrorWarn(event,event.getErrorMsg());
    }

    private boolean isOverTime(StatementEvent event){
        return event.getFireTime() - event.getGenerateTime() > timeout;
    }

    public void sendErrorWarn(StatementEvent event,String errorMsg){
        StatementProxy statementProxy = (StatementProxy)event.getSource();
        StringBuilder errorInfo = new StringBuilder();
        errorInfo.append("[").append(event.getEventType()).append("]");
        errorInfo.append("[").append(DateUtils.format(event.getFireTime())).append("]");
        errorInfo.append("[").append(statementProxy.getSql()).append("]");
        errorInfo.append(Arrays.toString(statementProxy.getArgs()));
        errorInfo.append(":").append(errorMsg);
        alert(errorInfo.toString());
    }

    public void sendBatchErrorWarn(StatementEvent event,String errorMsg){
        StatementProxy statementProxy = (StatementProxy)event.getSource();
        StringBuilder errorInfo = new StringBuilder();
        errorInfo.append("[").append(event.getEventType()).append("]");
        errorInfo.append("[").append(DateUtils.format(event.getFireTime())).append("]");
        errorInfo.append(statementProxy.getBatchedSql().stream().collect(Collectors.joining(",")));
        errorInfo.append(Arrays.toString(statementProxy.getArgs()));
        errorInfo.append(":").append(errorMsg);
        alert(errorInfo.toString());
    }

    @Override
    public String getWarnName() {
        return null;
    }

    @Override
    public int getWarnTime() {
        return 0;
    }
}
