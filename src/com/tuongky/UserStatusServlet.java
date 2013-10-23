package com.tuongky;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class UserStatusServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String username = (String) req.getAttribute(Constants.USERNAME_ATTRIBUTE);
    if (username == null) {
      username = "";
    }
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(JsonUtils.toJson("username", username));
  }
}
