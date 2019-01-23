package org.jdbc.monitor.common;

import org.jdbc.monitor.exceptoin.IllegalTimeException;

/**
 * 时间分布范围
 * @author: shi rui
 * @create: 2018-12-24 13:41
 */
public enum STAT_TIME_DISTRIBUTION implements STAT {
    /** 链接时间分布范围 */
    TIME_1MS("0-1毫秒次数", 0, 1),
    TIME_10MS("1-10毫秒次数", 1, 10),
    TIME_100MS("10-100毫秒次数", 10, 100),
    TIME_1000MS("100-1000毫秒次数", 100, 1000),
    TIME_10S("1-10秒次数", 1000, 10000),
    TIME_100S("10-100秒次数", 10000, 100000),
    TIME_1000S("100-1000秒次数", 100000, 1000000),
    TIME_1000S_BG("大于1000秒次数", 1000000, 0),
    ;

    private String name;
    /** min最小时间，max最大时间,单位毫秒 */
    private long min;
    private long max;

    STAT_TIME_DISTRIBUTION(String name, long min, long max){
        this.name = name;
        this.min = min;
        this.max = max;
    }

    /**
     * 根据时间求枚举值,算头不算尾
     * @param time
     * @return
     */
    public static STAT_TIME_DISTRIBUTION valueOf(long time){
        STAT_TIME_DISTRIBUTION[] values = STAT_TIME_DISTRIBUTION.values();
        for(STAT_TIME_DISTRIBUTION distribution: values){
            if(distribution.getMin() <= time){
                if(time < distribution.getMax()) {
                    return distribution;
                }
                if(distribution.getMax() == 0){
                    return distribution;
                }
            }
        }
        throw new IllegalTimeException(String.format("时间[%d]不在分布范围内",time));
    }

    @Override
    public String getName() {
        return name;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }
}
