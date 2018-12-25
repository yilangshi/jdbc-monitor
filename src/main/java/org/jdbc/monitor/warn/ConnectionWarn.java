package org.jdbc.monitor.warn;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.MonitorDriver;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.listener.AbstractConnectWarnListener;
import org.jdbc.monitor.listener.Warn;
import org.jdbc.monitor.util.DateUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: shi rui
 * @create: 2019-01-17 09:48
 */
@Slf4j
public class ConnectionWarn<E extends Warn> extends AbstractConnectWarnListener {

    private long lastErrorTime = 0;

    private ReentrantLock reentrantLock = new ReentrantLock();

    /** 恢复时间，单位毫秒 */
    private final long RESUME_TIME = 1000;

    public ConnectionWarn(final List<E> warnList){
        super(warnList);
    }

    /**
     * 处理事件
     * @param event
     */
    @Override
    public void onMonitorEvent(ConnectionEvent event) {
        log.debug("receive event:"+event.toString());
        switch (event.getEventType()){
            case CONN_OPEN:
                doConnectOpenEventHandler(event);
                break;
            case CONN_CLOSE:
                doConnectCloseEventHandler(event);
                break;
            default:
                //抛弃不处理的事件
        }
    }

    /**
     * 链接打开事件报警处理
     * @param event
     */
    private void doConnectOpenEventHandler(ConnectionEvent event){
        if(event.getState() == EVENT_STATE.FAIL){
            sendErrorWarn(event);
            lastErrorTime = event.getFireTime();
        }else{
            //链接恢复报警,如果距上次报警小于1s，链接正常打开，发恢复正常通知
            if(lastErrorTime != 0){
                reentrantLock.lock();
                try {
                    if ((event.getFireTime() - lastErrorTime) <= RESUME_TIME) {
                        sendResumeWarn(event);
                    }
                }finally {
                    lastErrorTime = 0;
                    reentrantLock.unlock();
                }
            }
        }
    }

    /**
     * 链接关闭事件报警处理
     * @param event
     */
    private void doConnectCloseEventHandler(ConnectionEvent event){
        if(event.getState() == EVENT_STATE.FAIL){
            sendErrorWarn(event);
        }
    }

    public void sendErrorWarn(ConnectionEvent event){
        MonitorDriver monitorDriver = (MonitorDriver)event.getSource();
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("[").append(event.getEventType()).append("]");
        errorMsg.append("[").append(DateUtils.format(event.getFireTime())).append("]");
        errorMsg.append("[").append(monitorDriver.getDbUrl()).append("]");
        errorMsg.append("[").append(monitorDriver.getDbUser()).append("]");
        errorMsg.append(":").append(event.getErrorMsg());
        alert(errorMsg.toString());
    }

    public void sendResumeWarn(ConnectionEvent event){
        MonitorDriver monitorDriver = (MonitorDriver)event.getSource();
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("[").append(event.getEventType()).append("]");
        errorMsg.append("[").append(DateUtils.format(event.getFireTime())).append("]");
        errorMsg.append("[").append(monitorDriver.getDbUrl()).append("]");
        errorMsg.append("[").append(monitorDriver.getDbUser()).append("]");
        errorMsg.append(":").append("resume to normal");
        alert(errorMsg.toString());
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
