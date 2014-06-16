package com.tuongky.servlet.mail;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.util.AuthUtils;
import com.tuongky.util.ValidationUtils;

@SuppressWarnings("serial")
public class SimpleEmailSenderServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(SimpleEmailSenderServlet.class.getName());

  private static final String CHARSET = "UTF-8";

  private static final String ADMIN_EMAIL_ADDRESS = "tuongkydatviet@gmail.com";
  private static final String ADMIN_EMAIL_PERSONAL = "tuongky.com Admin";

  private void send(String toAddress, String toName, String subject, String htmlContent) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(ADMIN_EMAIL_ADDRESS, ADMIN_EMAIL_PERSONAL, CHARSET));

      msg.addRecipient(Message.RecipientType.TO,
              new InternetAddress(toAddress, toName));

      msg.setSubject(
          MimeUtility.encodeText(subject, CHARSET, "Q"));

      Multipart mp = new MimeMultipart();

      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(htmlContent, "text/html");
      mp.addBodyPart(htmlPart);

      msg.setContent(mp);

      Transport.send(msg);
    } catch (Exception e) {
      logger.severe("Fail to send email to " + toAddress + " - " + toName);
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (!AuthUtils.allowAdminOnly(req, resp)) {
      return;
    }

    String subject = ValidationUtils.mustBeSet(req, resp, "subject");
    String content = ValidationUtils.mustBeSet(req, resp, "content");
    String emails = req.getParameter("emails");
    boolean toAll = req.getParameter("to_all") != null;
    send(emails, "testing", subject, content);
  }
}
