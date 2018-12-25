package org.jdbc.monitor;

import org.jdbc.monitor.common.ALERT_METHOD;
import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.excutor.SimpleMonitorEventMulticaster;
import org.jdbc.monitor.listener.MonitorEventListener;
import org.jdbc.monitor.statistics.ConnectionStatistics;
import org.jdbc.monitor.statistics.ProfileStatistics;
import org.jdbc.monitor.statistics.StatementStatistics;
import org.jdbc.monitor.statistics.Statistics;
import org.jdbc.monitor.util.ClassUtils;
import org.jdbc.monitor.util.PropertyUtils;
import org.jdbc.monitor.util.StringUtils;
import org.jdbc.monitor.warn.ConnectionWarn;
import org.jdbc.monitor.warn.StatementWarn;
import org.jdbc.monitor.warn.tool.ConsoleWarn;
import org.jdbc.monitor.listener.Warn;
import org.jdbc.monitor.listener.AbstractConnectWarnListener;
import org.jdbc.monitor.warn.tool.MailWarn;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static final Configuration instance = new Configuration();

    private SimpleMonitorEventMulticaster monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
    private ProfileStatistics profileStatistics;
    private final ConnectionStatistics connectionStatistics;
    private final ConnectionWarn connectionWarn;
    private final StatementStatistics statementStatistics;
    private final StatementWarn statementWarn;

    private final List<Warn> warnList;
    private final List<Statistics> statisticsList;

    private boolean resetEnable = PropertyUtils.getBoolean("statistics.reset.flag");

    private Configuration(){
        monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
        warnList = initAlert();
        //创建监听对象
        connectionWarn = new ConnectionWarn(warnList);
        statementWarn = new StatementWarn(warnList);
        //创建统计对象
        statisticsList = new ArrayList<>();
        profileStatistics = new ProfileStatistics();
        statisticsList.add(profileStatistics);
        connectionStatistics = new ConnectionStatistics();
        statisticsList.add(connectionStatistics);
        statementStatistics = new StatementStatistics();
        statisticsList.add(statementStatistics);

    }

    public List<Warn> initAlert(){
        String alertMethod = PropertyUtils.getString("warn.method.list");
        if(alertMethod == null || "".equals(alertMethod.trim())){
            return null;
        }
        List<Warn> list = new ArrayList<>();
        String[] methods = alertMethod.trim().toUpperCase().split(",");
        for(String method:methods){
            ALERT_METHOD alert = ALERT_METHOD.valueOf(method);
            switch (alert){
                case CONSOLE : list.add(new ConsoleWarn()); break;
                case EMAIL: list.add(new MailWarn()); break;
                case SMS:
                case EXT:
                    List<Warn> extWarns = getExtWarns();
                    if(extWarns != null && extWarns.size() >0){
                        list.addAll(extWarns);
                    }
                    break;
                default: list.add(new ConsoleWarn());
            }
        }
        return list;
    }

    private List<Warn> getExtWarns(){
        String extWarns = PropertyUtils.getString("warn.ext.list");
        if(StringUtils.isEmpty(extWarns)){
            return null;
        }
        String[] extWarnClasses = extWarns.split(",");
        List<Warn> extWarnInstance = new ArrayList<>();
        for(String extWarnClass:extWarnClasses){
            Object object = ClassUtils.getClassInstance(extWarnClass);
            if(object == null){
                continue;
            }
            if(ClassUtils.isAssignableValue(Warn.class,object)){
                extWarnInstance.add((Warn)object);
            }
        }
        return extWarnInstance;
    }

    /**
     * 添加连接监听
     */
    public void initListener(){
        addListener(connectionWarn);
        addListener(profileStatistics);
        addListener(connectionStatistics);
        addListener(statementWarn);
        addListener(statementStatistics);
    }


    public static Configuration getInstance(){
        return instance;
    }

    public List<Statistics> getStatisticsList() {
        return statisticsList;
    }

    public ConnectionStatistics getConnectionStatistics() {
        return connectionStatistics;
    }

    public ProfileStatistics getProfileStatistics() {
        return profileStatistics;
    }

    /**
     * 加入监听
     * @param listener
     * @param <T>
     */
    public <T extends MonitorEvent>  void addListener(MonitorEventListener<T> listener){
        monitorEventMulticaster.addMonitorEventListener(listener);
    }

    public boolean isResetEnable() {
        return resetEnable;
    }

    public StatementStatistics getStatementStatistics() {
        return statementStatistics;
    }


}
