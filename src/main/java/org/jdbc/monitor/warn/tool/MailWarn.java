package org.jdbc.monitor.warn.tool;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.listener.Warn;
import org.jdbc.monitor.util.PropertyUtils;
import org.jdbc.monitor.util.StringUtils;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件报警
 * @author: shi rui
 * @create: 2019-01-23 13:37
 */
@Slf4j
public class MailWarn implements Warn{

    private final String warnType = "JDBC-MONITOR-EMAIL-WARN";

    private final String appName = PropertyUtils.getString("app.name");

    private final String FROM_EMAIL_ACCOUNT = PropertyUtils.getString("warn.email.from.account");
    private final String FROM_EMAIL_PASSWORD = PropertyUtils.getString("warn.email.from.password");
    private final String FROM_EMAIL_SMTP = PropertyUtils.getString("warn.email.smtp");
    private final String FROM_EMAIL_PORT = PropertyUtils.getString("warn.email.port","25");
    private final String TO_EMAIL_ACCOUNT = PropertyUtils.getString("warn.email.to.account");
    private final Properties props = new Properties();

    private Session session = null;

    public MailWarn(){
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", FROM_EMAIL_SMTP);
        props.put("mail.smtp.port", FROM_EMAIL_PORT);
        session = getSession();
    }

    private Session getSession(){
        try {
            if(StringUtils.isEmpty(FROM_EMAIL_ACCOUNT) || StringUtils.isEmpty(FROM_EMAIL_PASSWORD)
                    || StringUtils.isEmpty(FROM_EMAIL_SMTP)){
                return null;
            }
            return Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM_EMAIL_ACCOUNT, FROM_EMAIL_PASSWORD);
                        }
                    });
        }catch (Exception e){
            log.error("连接邮件服务器出错，请检查配置:",e);
            return null;
        }
    }

    @Override
    public void alert(String msg) {
        if(StringUtils.isEmpty(TO_EMAIL_ACCOUNT)){
            return;
        }
        if(session == null){
            session = getSession();
        }
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL_ACCOUNT));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(TO_EMAIL_ACCOUNT));
            message.setSubject(getWarnHeader());
            StringBuilder content = new StringBuilder();
            content.append("您好：\n");
            content.append("     ");
            content.append(StringUtils.isEmpty(appName)?"":("应用["+appName+"]"));
            content.append("发生数据库报警: \n");
            content.append(msg);
            message.setText(content.toString());
            Transport.send(message);
        } catch (Exception e) {
            log.error("发送邮件异常:",e);
        }
    }

    @Override
    public String getWarnHeader() {
        return (StringUtils.isEmpty(appName)?"":("应用["+appName+"]"))+"触发["+warnType+"]报警";
    }
}
