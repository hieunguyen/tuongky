package com.tuongky.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by sngo on 3/2/14.
 */
public class EmailService {

  private static final Logger log = Logger.getLogger(EmailService.class.getName());
  public static final EmailService instance = new EmailService();

  private static final String QUEUE_NAME = "email_queue";

  private static final String ADMIN_EMAIL_ADDRESS = "tuongky@gmail.com";

  private String getMailHtml(String template, Map<String, Object> contentMap) {
    Mustache mustache = new DefaultMustacheFactory().compile(template);
    StringWriter writer = new StringWriter();
    mustache.execute(writer, contentMap);

    return writer.toString();
  }

  public void send(String address, MailTemplate template, Map<String, Object> contentMap){
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(ADMIN_EMAIL_ADDRESS, "Example.com Admin"));

      msg.addRecipient(Message.RecipientType.TO,
              new InternetAddress(address, "Mr. Son"));

      msg.setSubject(template.subject);

      msg.setText(getMailHtml(template.template, contentMap));

      Transport.send(msg);

    } catch (Exception e) {
      log.severe("Fail to send email to " + address);
    }
  }
}
