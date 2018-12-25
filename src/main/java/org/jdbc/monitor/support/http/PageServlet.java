package org.jdbc.monitor.support.http;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;

/**
 * copy from druid
 * @author: shi rui
 * @create: 2019-01-22 10:56
 */
@Slf4j
public class PageServlet  extends ResourceServlet {

    private static final long     serialVersionUID        = 1L;

    private final StatisticsService statisticsService;

    public PageServlet(){
        super("support/http/");
        statisticsService = new StatisticsService();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    /**
     *  真实的线上服务器是不开放非80端口，所以jmx没必要
     * @param url 要连接的服务地址
     * @return 调用服务后返回的json字符串
     */
    @Override
    protected String process(String url) {
        return statisticsService.service(url);
    }
}
