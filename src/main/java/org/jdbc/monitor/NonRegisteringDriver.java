package org.jdbc.monitor;

import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.event.ConnectionEvent;
import org.jdbc.monitor.event.EVENT_STATE;
import org.jdbc.monitor.event.EventSupport;
import org.jdbc.monitor.event.type.CONN_EVENT;
import org.jdbc.monitor.proxy.ConnectionFactory;
import org.jdbc.monitor.util.DriverUtils;
import org.jdbc.monitor.util.PropertyUtils;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author: shi rui
 * @create: 2018-12-13 11:01
 */
public class NonRegisteringDriver extends EventSupport implements Driver {

    public final String version;
    public final String name;

    private String dbUrl;
    private String dbUser;

    /** 目标驱动 */
    protected Driver targetDriver;

    public NonRegisteringDriver(Driver targetDriver){
        super();
        this.targetDriver = targetDriver;
        this.name = PropertyUtils.getString(Constant.MONITER_DRIVER_NAME);
        this.version = PropertyUtils.getString(Constant.MONITER_DRIVER_VERSION);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        long startTime = System.currentTimeMillis();
        try {
            this.dbUrl = url;
            this.dbUser = info.getProperty("user");
            Connection connection = targetDriver.connect(DriverUtils.getTargetDriverUrl(url), info);
            publishEvent(ConnectionEvent.build(this, CONN_EVENT.CONN_OPEN, startTime,System.currentTimeMillis()));
            return ConnectionFactory.createConnection(connection);
        }catch (Exception e){
            publishEvent(ConnectionEvent.build(this, CONN_EVENT.CONN_OPEN, startTime,System.currentTimeMillis(), EVENT_STATE.FAIL, e.getMessage()));
            throw e;
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url.startsWith(Constant.URL_PREFIX)) {
            return targetDriver.acceptsURL(DriverUtils.getTargetDriverUrl(url));
        } else {
            return false;
        }
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return targetDriver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return targetDriver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return targetDriver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return targetDriver.getParentLogger();
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public Driver getTargetDriver() {
        return targetDriver;
    }
}
