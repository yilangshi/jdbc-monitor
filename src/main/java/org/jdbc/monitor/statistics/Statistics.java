package org.jdbc.monitor.statistics;

import org.jdbc.monitor.common.STAT;

import java.util.Map;

/**
 * @author: shi rui
 * @create: 2018-12-18 11:10
 */
public interface Statistics<E extends STAT> {

    Map<E,Object> getStatistics();

    String getStatisticsInfo();

    void clearStatistics();

}
