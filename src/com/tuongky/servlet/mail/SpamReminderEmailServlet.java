package com.tuongky.servlet.mail;

import com.tuongky.service.MailTemplate;
import com.tuongky.service.email.EmailSendService;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by sngo on 3/9/14.
 */
public class SpamReminderEmailServlet extends HttpServlet{
  private static final Logger log = Logger.getLogger(WelcomeEmailServlet.class.getName());

  public static String ADDRESS_FIELD = "address";
  public static String USER_NAME_FIELD = "name";

  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String address = req.getParameter(ADDRESS_FIELD);
    String userName = req.getParameter(USER_NAME_FIELD);

    if (address == null){
      log.severe("WelcomeEmailServlet fail: address = " + address);
      return;
    }
    if (userName == null){
      log.warning("WelcomeEmailServlet name is null");
    }

    Map<String, String> contentMap = new HashMap<>();
    contentMap.put(USER_NAME_FIELD, userName);

    EmailSendService.instance.send(address, userName, MailTemplate.SPAM_REMINDER, contentMap);
  }

}
