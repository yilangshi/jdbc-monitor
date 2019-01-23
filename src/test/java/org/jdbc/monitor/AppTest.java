package org.jdbc.monitor;

import org.jdbc.monitor.event.MonitorEvent;
import org.jdbc.monitor.event.type.CONN_EVENT_TYPE;
import org.jdbc.monitor.excutor.SimpleMonitorEventMulticaster;
import org.jdbc.monitor.listener.AbstractMonitorEventListener;
import org.jdbc.monitor.statistics.Statistics;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
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
            resultSet.last();
            System.out.println("数据库数据:"+resultSet.getRow());
            resultSet.beforeFirst();
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
                .addMonitorEventListener(new AbstractMonitorEventListener<MonitorEvent>(CONN_EVENT_TYPE.CONN_OPEN){
            @Override
            public void onMonitorEvent(MonitorEvent event) {
                System.out.println("fire event========== "+ event.toString());
            }
        });
        SimpleMonitorEventMulticaster.getInstance()
                .addMonitorEventListener(new AbstractMonitorEventListener<MonitorEvent>(CONN_EVENT_TYPE.CONN_CLOSE){
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
    public void testErrorAlert(){
        //测试异常报警
        testConnecton("root","error");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String statisticsInfo = Configuration.getInstance().getConnectionStatistics().getStatisticsInfo();
        System.out.println("================");
        System.out.println(statisticsInfo);
        System.out.println("================");
    }

    @Test
    public void testErrorResumeAlert(){
        //测试异常报警
        testConnecton("root","error");
        //测试恢复报警
        testConnecton();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String statisticsInfo = Configuration.getInstance().getConnectionStatistics().getStatisticsInfo();
        System.out.println("================");
        System.out.println(statisticsInfo);
        System.out.println("================");
    }

    @Test
    public void testBatch(){
        try {
            Class.forName("org.jdbc.monitor.MonitorDriver");
            Connection conn  = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.addBatch("update test set name = 'bbbb' where id = 1");
            statement.addBatch("update test set name = 'cccc' where id = 2");
            int[] aa = statement.executeBatch();

            System.out.println("result:"+Arrays.toString(aa));

            statement.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testForbitWord(){
        try {
            Class.forName("org.jdbc.monitor.MonitorDriver");
            Connection conn  = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            Statement statement = conn.createStatement();
            int aa = statement.executeUpdate("delete from test where id=2");

            System.out.println("result:"+aa);

            statement.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testWarnWord(){
        try {
            Class.forName("org.jdbc.monitor.MonitorDriver");
            Connection conn  = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            Statement statement = conn.createStatement();
            int aa = statement.executeUpdate("update test set name='<script>alert()</script>' id=2");

            System.out.println("result:"+aa);

            statement.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testWaitTimeOut(){

    }
}
