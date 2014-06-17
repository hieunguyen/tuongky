package com.tuongky.servlet.mail;

import java.io.IOException;
import java.util.List;
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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.User;
import com.tuongky.util.AuthUtils;
import com.tuongky.util.ValidationUtils;

@SuppressWarnings("serial")
public class SimpleEmailSenderServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(SimpleEmailSenderServlet.class.getName());

  private static final String CHARSET = "UTF-8";

  private static final String ADMIN_EMAIL_ADDRESS = "tuongkydatviet@gmail.com";
  private static final String ADMIN_EMAIL_PERSONAL = "tuongky.com Admin";

  private static final String DEFAULT_TO_NAME = "Bạn yêu cờ";

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
    if (toAll) {
      List<User> users = UserDao.instance.getAll();
      for (User user : users) {
        if (Strings.isNullOrEmpty(user.getFbId()) || Strings.isNullOrEmpty(user.getEmail())) {
          continue;
        }
        String toName = Strings.isNullOrEmpty(user.getFbName()) ?
            DEFAULT_TO_NAME : user.getFbName();
        send(user.getEmail(), toName, subject, content);
      }
    } else {
      List<String> toAddresses = Lists.newArrayList(Splitter
          .on(",")
          .trimResults()
          .omitEmptyStrings()
          .split(emails));
      for (String toAddress : toAddresses) {
        send(toAddress, DEFAULT_TO_NAME, subject, content);
      }
    }
  }
}
