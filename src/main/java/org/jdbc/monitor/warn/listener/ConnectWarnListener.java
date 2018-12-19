package org.jdbc.monitor.warn.listener;

import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.listener.AbstractConnectionMonitorEventListener;
import org.jdbc.monitor.warn.Warn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:50
 */
public class ConnectWarnListener<E extends Warn> extends AbstractConnectionMonitorEventListener implements WarnListener<E> {

    List<E> warnList = new ArrayList<>();

    public ConnectWarnListener(final List<E> warnList){
        this.warnList = warnList;
    }

    @Override
    public void onMonitorEvent(ConnectionEvent event) {
        if(event.getState() == EVENT_STATE.FAIL){
            alert(event.getEventType()+":"+event.getErrorMsg());
        }
    }

    @Override
    public void addWarn(E warn) {
        warnList.add(warn);
    }

    @Override
    public void alert(String msg) {
        for(Warn warn:warnList){
            warn.alert(msg);
        }
    }
}
