package org.jdbc.monitor;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.common.Constant;
import org.jdbc.monitor.exceptoin.RegisterException;
import org.jdbc.monitor.util.DriverUtils;
import org.jdbc.monitor.util.PropertyUtils;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 实现代理驱动
 * @author shirui
 * @date 2018-12-11 19:25:32
 */
@Slf4j
public class MonitorDriver  extends NonRegisteringDriver implements Driver {

    static {
        String targetDriverClass = PropertyUtils.getString(Constant.PROXY_TARGET_DRIVER);
        try {
            Driver driver = DriverUtils.getDriver(targetDriverClass);
            if(driver == null){
                throw new RegisterException(String.format("未发现数据库驱动{%s}",targetDriverClass));
            }
            DriverManager.deregisterDriver(driver);
            DriverManager.registerDriver(new MonitorDriver(driver));
            Configuration.getInstance().initListener();
        } catch (ClassNotFoundException e) {
            log.error("未发现数据库驱动{{}}",targetDriverClass,e);
        } catch (SQLException e) {
            log.error("注册数据库驱动{{}}异常",targetDriverClass,e);
        }
    }

    public MonitorDriver(Driver targetDriver){
        super(targetDriver);
    }


}
