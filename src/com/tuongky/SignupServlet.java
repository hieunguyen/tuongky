package com.tuongky;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.InviteDao;
import com.tuongky.backend.SessionDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.Invite;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.User;

@SuppressWarnings("serial")
public class SignupServlet extends HttpServlet {

  private static final String USERNAME_PATTERN = "[a-zA-Z0-9_]+";
  private static final String PASSWORD_PATTERN = "\\S+";
  private static final int MIN_LENGTH_USERNAME = 3;
  private static final int MAX_LENGTH_USERNAME = 30;
  private static final int MIN_LENGTH_PASSWORD = 5;
  private static final int MAX_LENGTH_PASSWORD = 50;

  private boolean isValid(String username, String password) {
    if (username.length() < MIN_LENGTH_USERNAME || username.length() > MAX_LENGTH_USERNAME ||
        !username.matches(USERNAME_PATTERN)) {
      return false;
    }
    if (password.length() < MIN_LENGTH_PASSWORD || password.length() > MAX_LENGTH_PASSWORD ||
        !password.matches(PASSWORD_PATTERN)) {
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

    String inviteCode = req.getParameter("invite_code");
    boolean inviteOk = true;
    if (Settings.BETA) {
      inviteOk = false;
      if (inviteCode != null && inviteCode.trim().length() > 0) {
        InviteDao inviteDao = new InviteDao();
        Invite invite = inviteDao.getById(inviteCode.trim());
        if (invite != null && !invite.hasUsed()) {
          inviteOk = true;
          invite.useIt();
          inviteDao.save(invite);
        }
      }
    }

    int code = 0; // Valid
    UserDao userDao = new UserDao();
    User user = userDao.getByUsername(username);
    if (user != null) {
      code = 1; // Existing username.
    } else if (!isValid(username, password)) {
      code = 2; // All other invalid cases (should be already tested on the client side).
    } else if (!inviteOk) {
      code = 3;
    }
    Map<String, Object> data = Maps.newHashMap();
    data.put("code", code);
    if (code == 0) {
      user = userDao.save(email, username, password);
      data.put("username", username);
      Session session = new SessionDao().save(user.getId());
      data.put("sid", session.getId());
    }
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(new Gson().toJson(data));
  }
}
