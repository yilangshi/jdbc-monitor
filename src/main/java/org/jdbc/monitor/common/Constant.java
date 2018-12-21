package org.jdbc.monitor.common;

/**
 * 常量定义
 * @author: shi rui
 * @create: 2018-12-12 14:19
 */
public final class Constant {

    /** 驱动名称 */
    public static final String MONITER_DRIVER_NAME = "moniter.driver.name";

    /** 驱动版本 */
    public static final String MONITER_DRIVER_VERSION = "moniter.driver.version";

    /** 代理驱动url前缀 **/
    public static final String URL_PREFIX = "proxy:";

    /** 被代理驱动 */
    public static final String PROXY_TARGET_DRIVER = "proxy.target.driver";

    /** 信息中存放最多的数据条数,多于此条数，丢弃时间最久的 */
    public static final int LIST_MAX_SIZE = 1024;




}
