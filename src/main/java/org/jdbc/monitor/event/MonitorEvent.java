package org.jdbc.monitor.event;

import org.jdbc.monitor.event.type.EventType;

import java.io.Serializable;
import java.util.EventObject;

/**
 * @author: shi rui
 * @create: 2018-12-13 19:27
 */
public class MonitorEvent extends EventObject implements Serializable{

    private final EventType eventType;

    /** System time when the event happened. */
    private final long generateTime;

    protected final long fireTime;
    /** 状态：0成功，1异常 */
    private final EVENT_STATE state;
    /** 异常信息 */
    private final String errorMsg;


    public MonitorEvent(Object source){
        super(source);
        this.eventType = null;
        this.generateTime = System.currentTimeMillis();
        this.state = EVENT_STATE.SUCCESS;
        this.fireTime = 0;
        this.errorMsg = null;
    }

    public MonitorEvent(Object source,EventType eventType){
        super(source);
        this.eventType = eventType;
        this.generateTime = System.currentTimeMillis();
        this.state = EVENT_STATE.SUCCESS;
        this.fireTime = 0;
        this.errorMsg = null;
    }

    public MonitorEvent(Object source,EventType eventType,long generateTime,long fireTime){
        super(source);
        this.eventType = eventType;
        this.generateTime = generateTime;
        this.state = EVENT_STATE.SUCCESS;
        this.fireTime = fireTime;
        this.errorMsg = null;
    }

    public MonitorEvent(Object source,EventType eventType,long generateTime, long fireTime, EVENT_STATE state, String errorMsg){
        super(source);
        this.eventType = eventType;
        this.generateTime = generateTime;
        this.state = state;
        this.fireTime = fireTime;
        this.errorMsg = errorMsg;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public long getFireTime() {
        return fireTime;
    }

    @Override
    public String toString() {
        return "MonitorEvent{" +
                "eventType=" + eventType +
                ", generateTime=" + generateTime +
                ", fireTime=" + fireTime +
                ", state=" + state +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

    public EVENT_STATE getState() {
        return state;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
