package com.tuongky.servlet.mail;

import com.tuongky.service.EmailService;

import javax.servlet.http.HttpServlet;

/**
 * Created by sngo on 3/2/14.
 */
public class MailHandlerServlet extends HttpServlet {

  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {

    EmailService.instance.send(req.getParameter("address"), "Congratulations");

  }

}
