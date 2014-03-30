package com.tuongky.service.email;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.tuongky.service.MailTemplate;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by sngo on 3/2/14.
 */
public class EmailSendService {

  private static final Logger log = Logger.getLogger(EmailSendService.class.getName());
  public static final EmailSendService instance = new EmailSendService();

  private static final String ADMIN_EMAIL_ADDRESS = "hieu.ngvan@gmail.com";
  private static final String ADMIN_EMAIL_PERSONAL = "tuongky.com Admin";

  private String getMailHtml(String template, Map<String, String> contentMap) {
    Mustache mustache = new DefaultMustacheFactory().compile(template);
    StringWriter writer = new StringWriter();
    mustache.execute(writer, contentMap);

    return writer.toString();
  }

  public void send(String address, String personal, MailTemplate template, Map<String, String> contentMap){
    log.info("Sending " + template.name() + " to " + address);

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(ADMIN_EMAIL_ADDRESS, ADMIN_EMAIL_PERSONAL));

      msg.addRecipient(Message.RecipientType.TO,
              new InternetAddress(address, personal));

      msg.setSubject(template.getSubject());

      Multipart mp = new MimeMultipart();

      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(getMailHtml(template.getTemplate(), contentMap), "text/html");
      mp.addBodyPart(htmlPart);

      msg.setContent(mp);

      Transport.send(msg);
      log.info("Successfully sent " + template.name() + " to " + address);
    } catch (Exception e) {
      log.severe("Fail to send email to " + address + " " + stackTraceToString(e));
    }
  }

  private String stackTraceToString(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }
}
