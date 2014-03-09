package com.tuongky.servlet.mail;

import com.tuongky.service.email.EmailSendService;
import com.tuongky.service.MailTemplate;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by sngo on 3/9/14.
 */
public class LevelUpEmailServlet extends HttpServlet{

  private static final Logger log = Logger.getLogger(WelcomeEmailServlet.class.getName());

  private static String ADDRESS_FIELD = "address";
  private static String USER_NAME_FIELD = "name";
  private static String OLD_LEVEL_FIELD = "old_level";
  private static String NEW_LEVEL_FIELD = "new_level";

  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String address = req.getParameter(ADDRESS_FIELD);
    String userName = req.getParameter(USER_NAME_FIELD);
    String oldLevel = req.getParameter(OLD_LEVEL_FIELD);
    String newLevel = req.getParameter(NEW_LEVEL_FIELD);

    if (address == null){
      log.severe("WelcomeEmailServlet fail: address = " + address);
      return;
    }

    if (userName == null){
      log.warning("WelcomeEmailServlet name is null");
    }

    Map<String, String> contentMap = new HashMap<>();
    contentMap.put(USER_NAME_FIELD, userName);
    contentMap.put(OLD_LEVEL_FIELD, oldLevel);
    contentMap.put(NEW_LEVEL_FIELD, newLevel);

    EmailSendService.instance.send(address, userName, MailTemplate.LEVEL_UP, contentMap);
  }
}
