package com.tuongky.service;

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
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by sngo on 3/2/14.
 */
public class EmailService {

  private static final Logger log = Logger.getLogger(EmailService.class.getName());
  public static final EmailService instance = new EmailService();

  private static final String QUEUE_NAME = "email_queue";

  public void send(String address, String body){
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("ntson22@gmail.com", "Example.com Admin"));

      msg.addRecipient(Message.RecipientType.TO,
              new InternetAddress(address, "Mr. Son"));

      msg.setSubject("Your Rank has been updated");

      msg.setText(body);

      Transport.send(msg);

    } catch (Exception e) {
      log.severe("Fail to send email to " + address);
    }
  }
}
