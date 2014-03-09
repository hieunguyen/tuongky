package com.tuongky.servlet.mail;

import com.tuongky.service.EmailService;
import com.tuongky.service.MailTemplate;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sngo on 3/2/14.
 */
public class MailHandlerServlet extends HttpServlet {

  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {

    Map<String, Object> map = new HashMap<>();
    map.put("name", "son dep trai");

    EmailService.instance.send(req.getParameter("address"), MailTemplate.FIRST_TIMER, map);

  }

}
