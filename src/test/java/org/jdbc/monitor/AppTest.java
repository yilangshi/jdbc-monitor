package org.jdbc.monitor;

import static org.junit.Assert.assertTrue;

import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.event.type.CONN_EVENT;
import org.jdbc.monitor.excutor.SimpleMonitorEventMulticaster;
import org.jdbc.monitor.listener.AbstractMonitorEventListener;
import org.jdbc.monitor.statistics.Statistics;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    public static final String JDBC_URL = "proxy:jdbc:mysql://127.0.0.1:3306/testdb?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&connectTimeout=1000&socketTimeout=3000";

    public static final String USER_NAME = "root";

    public static final String PASSWORD = "sr0210";

    @Test
    public void testConnecton(){
        testConnecton(USER_NAME,PASSWORD);
    }

    private void testConnecton(String userName,String password){
        try {
            Class.forName("org.jdbc.monitor.MonitorDriver");
            Connection conn  = DriverManager.getConnection(JDBC_URL, userName, password);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from test ");
            System.out.print("数据库数据:");
            while (resultSet.next()){
                System.out.print(resultSet.getString(1) + ", ");
                System.out.println(resultSet.getString(2));
            }
            resultSet.close();
            statement.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testEvent(){
        SimpleMonitorEventMulticaster.getInstance()
                .addMonitorEventListener(new AbstractMonitorEventListener<MonitorEvent>(CONN_EVENT.CONN_OPEN){
            @Override
            public void onMonitorEvent(MonitorEvent event) {
                System.out.println("fire event========== "+ event.toString());
            }
        });
        SimpleMonitorEventMulticaster.getInstance()
                .addMonitorEventListener(new AbstractMonitorEventListener<MonitorEvent>(CONN_EVENT.CONN_CLOSE){
                    @Override
                    public void onMonitorEvent(MonitorEvent event) {
                        System.out.println("fire event========== "+ event.toString());
                    }
                });
        testConnecton();
        System.out.println();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStatistics(){
        testConnecton();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Statistics> statisticsList = Configuration.getInstance().getStatisticsList();
        for(Statistics statistics:statisticsList) {
            System.out.println("================");
            System.out.println(statistics.getStatisticsInfo());
        }
        System.out.println("================");
    }

    @Test
    public void testAlert(){
        testConnecton("root","error");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String statisticsInfo = Configuration.getInstance().getConnectionStatistics().getStatisticsInfo();
        System.out.println("================");
        System.out.println(statisticsInfo);
        System.out.println("================");
    }
}
