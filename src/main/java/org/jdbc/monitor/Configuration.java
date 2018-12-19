package org.jdbc.monitor;

import org.jdbc.monitor.common.ALERT_METHOD;
import org.jdbc.monitor.excutor.SimpleMonitorEventMulticaster;
import org.jdbc.monitor.statistics.ConnectionStatistics;
import org.jdbc.monitor.util.PropertyUtils;
import org.jdbc.monitor.warn.ConsoleWarn;
import org.jdbc.monitor.warn.Warn;
import org.jdbc.monitor.warn.listener.ConnectWarnListener;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static final Configuration instance = new Configuration();

    private SimpleMonitorEventMulticaster monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
    private final ConnectionStatistics connectionStatistics;
    private final ConnectWarnListener connectWarnListener;

    private final List<Warn> warnList;

    private Configuration(){
        monitorEventMulticaster = SimpleMonitorEventMulticaster.getInstance();
        connectionStatistics = new ConnectionStatistics();
        warnList = initAlert();
        connectWarnListener = new ConnectWarnListener(warnList);
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
        monitorEventMulticaster.addMonitorEventListener(connectionStatistics);
        monitorEventMulticaster.addMonitorEventListener(connectWarnListener);
    }


    public static Configuration getInstance(){
        return instance;
    }


    public ConnectionStatistics getConnectionStatistics() {
        return connectionStatistics;
    }
}
