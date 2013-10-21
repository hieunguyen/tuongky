package com.tuongky;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.SessionDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.User;

@SuppressWarnings("serial")
public class SignupServlet extends HttpServlet {

  private static final String USERNAME_PATTERN = "[a-z0-9_]+";
  private static final String PASSWORD_PATTERN = "[A-Za-z0-9_]+";

  private boolean isValid(String username, String password) {
    if (username.length() < 2 || !username.matches(USERNAME_PATTERN)) {
      return false;
    }
    if (password.length() < 6 || !password.matches(PASSWORD_PATTERN)) {
      return false;
    }
    return true;
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String email = req.getParameter("email");
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    int code = 0; // Valid
    UserDao userDao = new UserDao();
    User user = userDao.getByUsername(username);
    if (user != null) {
      code = 1; // Existing username.
    } else if (!isValid(username, password)) {
      code = 2; // All other invalid cases (should be already tested on the client side).
    }
    Map<String, Object> data = Maps.newHashMap();
    data.put("code", code);
    if (code == 0) {
      user = userDao.save(email, username, password);
      data.put("username", username);
      SessionDao sessionDao = new SessionDao();
      Session session = sessionDao.save(user.getId(), user.getUsername());
      data.put("sid", session.getId());
    }
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(new Gson().toJson(data));
  }
}