package org.jdbc.monitor.statistics;

import org.jdbc.monitor.MonitorDriver;
import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.common.STAT_PROFILE;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.type.CONN_EVENT_TYPE;
import org.jdbc.monitor.listener.AbstractConnectionMonitorEventListener;
import org.jdbc.monitor.util.DateUtils;
import org.jdbc.monitor.util.PropertyUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 概要信息统计
 * @author: shi rui
 * @create: 2018-12-20 11:39
 */
public class ProfileStatistics extends AbstractConnectionMonitorEventListener implements Statistics<STAT_PROFILE> {

    private final String[] profileDriverInfo = new String[2];
    private final String[] profileDriverVersion = new String[2];
    private final String profileJavaVersion;
    private final String profileJvmName;
    private final String profileClassPath;
    private String profileDbUrl;
    private String profileDbUser;
    private final String profileStartTime;
    private final boolean profileResetFlag;
    private final AtomicInteger profileResetTime;

    @Override
    public void onMonitorEvent(ConnectionEvent event) {
        if(profileDbUrl != null && profileDbUrl != ""){
            return;
        }
        if(event.getEventType() != CONN_EVENT_TYPE.CONN_OPEN && event.getState() == EVENT_STATE.SUCCESS){
            return;
        }
        MonitorDriver monitorDriver = (MonitorDriver)event.getSource();
        profileDriverInfo[0] = monitorDriver.getName();
        profileDriverInfo[1] = PropertyUtils.getString(Constant.PROXY_TARGET_DRIVER);
        profileDriverVersion[0] = monitorDriver.getVersion();
        profileDriverVersion[1] = String.format("%d.%d.x",monitorDriver.getTargetDriver().getMajorVersion(),monitorDriver.getTargetDriver().getMinorVersion());
        profileDbUrl = monitorDriver.getDbUrl();
        profileDbUser = monitorDriver.getDbUser();
    }

    public ProfileStatistics(){
        profileJavaVersion = System.getProperty("java.version");
        profileJvmName = System.getProperty("java.vm.name");
        profileClassPath = System.getProperty("java.class.path");
        profileStartTime = DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:sss");
        profileResetFlag = PropertyUtils.getBoolean("statistics_reset_flag");
        profileResetTime = new AtomicInteger();
    }

    @Override
    public Map<STAT_PROFILE,Object> getStatistics() {
        Map<STAT_PROFILE,Object> map = new HashMap<>(16);
        map.put(STAT_PROFILE.PROFILE_DRIVER_NAME,profileDriverInfo);
        map.put(STAT_PROFILE.PROFILE_DRIVER_VERSION,profileDriverVersion);
        map.put(STAT_PROFILE.PROFILE_JAVA_VERSION,profileJavaVersion);
        map.put(STAT_PROFILE.PROFILE_JVM_NAME,profileJvmName);
        map.put(STAT_PROFILE.PROFILE_CLASS_PATH,profileClassPath);
        map.put(STAT_PROFILE.PROFILE_DB_URL,profileDbUrl);
        map.put(STAT_PROFILE.PROFILE_DB_USER,profileDbUser);
        map.put(STAT_PROFILE.PROFILE_START_TIME,profileStartTime);
        map.put(STAT_PROFILE.PROFILE_RESET_FLAG,profileResetFlag);
        map.put(STAT_PROFILE.PROFILE_RESET_TIME,profileResetTime);
        return null;
    }

    @Override
    public String getStatisticsInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(STAT_PROFILE.PROFILE_DRIVER_NAME.getName()).append(":").append(getDriverInfoStr()).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_JAVA_VERSION.getName()).append(":").append(profileJavaVersion).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_JVM_NAME.getName()).append(":").append(profileJvmName).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_CLASS_PATH.getName()).append(":").append(profileClassPath).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_DB_URL.getName()).append(":").append(profileDbUrl).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_DB_USER.getName()).append(":").append(profileDbUser).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_START_TIME.getName()).append(":").append(profileStartTime).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_RESET_FLAG.getName()).append(":").append(profileResetFlag).append("\r\n");
        stringBuilder.append(STAT_PROFILE.PROFILE_RESET_TIME.getName()).append(":").append(profileResetTime).append("\r\n");
        return stringBuilder.toString();
    }

    private String getDriverInfoStr(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(profileDriverInfo[0]).append("-").append(profileDriverVersion[0]).append(",")
                .append(profileDriverInfo[1]).append("-").append(profileDriverVersion[1]);
        return stringBuilder.toString();
    }

    @Override
    public void clearStatistics() {
        this.profileResetTime.set(0);
    }

}
