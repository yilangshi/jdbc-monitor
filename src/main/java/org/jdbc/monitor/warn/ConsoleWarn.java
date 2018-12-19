package org.jdbc.monitor.warn;

/**
 * @author: shi rui
 * @create: 2018-12-18 17:54
 */
public class ConsoleWarn implements Warn {
    @Override
    public void alert(String msg) {
        System.err.println("异常-"+msg);
    }
}
