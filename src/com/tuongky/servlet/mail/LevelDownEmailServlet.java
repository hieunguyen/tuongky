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
@SuppressWarnings("serial")
public class LevelDownEmailServlet extends HttpServlet{
  private static final Logger log = Logger.getLogger(LevelDownEmailServlet.class.getName());

  public static String ADDRESS_FIELD = "address";
  public static String USER_NAME_FIELD = "name";
  public static String OLD_LEVEL_FIELD = "old_level";
  public static String NEW_LEVEL_FIELD = "new_level";

  @Override
  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String address = req.getParameter(ADDRESS_FIELD);
    String userName = req.getParameter(USER_NAME_FIELD);
    String oldLevel = req.getParameter(OLD_LEVEL_FIELD);
    String newLevel = req.getParameter(NEW_LEVEL_FIELD);

    if (address == null){
      log.severe("LevelDownEmailServlet fail: address = " + address);
      return;
    }

    if (userName == null){
      log.warning("LevelDownEmailServlet name is null");
    }

    Map<String, String> contentMap = new HashMap<>();
    contentMap.put(USER_NAME_FIELD, userName);
    contentMap.put(OLD_LEVEL_FIELD, oldLevel);
    contentMap.put(NEW_LEVEL_FIELD, newLevel);

    EmailSendService.instance.send(address, userName, MailTemplate.LEVEL_DOWN, contentMap);
  }

}
