package org.jdbc.monitor;

import org.jdbc.monitor.common.ALERT_METHOD;
import org.jdbc.monitor.excutor.SimpleMonitorEventMulticaster;
import org.jdbc.monitor.statistics.ConnectionStatistics;
import org.jdbc.monitor.statistics.ProfileStatistics;
import org.jdbc.monitor.statistics.Statistics;
import org.jdbc.monitor.util.PropertyUtils;
import org.jdbc.monitor.warn.ConsoleWarn;
import org.jdbc.monitor.warn.Warn;
import org.jdbc.monitor.warn.listener.ConnectWarnListener;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static final Configuration instance = new Configuration();

    private SimpleMonitorEventMulticaster monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
    private ProfileStatistics profileStatistics;
    private final ConnectionStatistics connectionStatistics;
    private final ConnectWarnListener connectWarnListener;

    private final List<Warn> warnList;
    private final List<Statistics> statisticsList;

    private Configuration(){
        monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
        warnList = initAlert();
        //创建监听对象
        connectWarnListener = new ConnectWarnListener(warnList);
        //创建统计对象
        statisticsList = new ArrayList<>();
        profileStatistics = new ProfileStatistics();
        statisticsList.add(profileStatistics);
        connectionStatistics = new ConnectionStatistics();
        statisticsList.add(connectionStatistics);

    }

    public List<Warn> initAlert(){
        String alertMethod = PropertyUtils.getString("warn.list");
        if(alertMethod == null || "".equals(alertMethod.trim())){
            return null;
        }
        List<Warn> list = new ArrayList<>();
        String[] methods = alertMethod.trim().toUpperCase().split(",");
        for(String method:methods){
            ALERT_METHOD alert = ALERT_METHOD.valueOf(method);
            switch (alert){
                case CONSOLE : list.add(new ConsoleWarn()); break;
                case EMAIL:
                case SMS:
                case EXT:
                default: list.add(new ConsoleWarn());
            }
        }
        return list;
    }

    /**
     * 添加连接监听
     */
    public void initListener(){
        monitorEventMulticaster.addMonitorEventListener(connectWarnListener);
        monitorEventMulticaster.addMonitorEventListener(profileStatistics);
        monitorEventMulticaster.addMonitorEventListener(connectionStatistics);
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
}
