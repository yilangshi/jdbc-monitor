package org.jdbc.monitor.warn.tool;

import org.jdbc.monitor.Configuration;
import org.jdbc.monitor.listener.Warn;
import org.jdbc.monitor.util.StringUtils;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:54
 */
public class ConsoleWarn implements Warn {

    private final String warnType = "JDBC-MONITOR-CONSOLE-WARN";

    @Override
    public void alert(String msg) {
        System.err.println(getWarnHeader()+":"+msg);
    }


    @Override
    public String getWarnHeader() {
        String appName = Configuration.getInstance().getProfileStatistics().getAppName();
        return (StringUtils.isEmpty(appName)?"":("["+appName+"]"))+"["+warnType+"]";
    }
}
