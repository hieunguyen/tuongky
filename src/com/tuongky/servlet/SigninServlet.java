package com.tuongky.servlet;

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
public class SigninServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String username = req.getParameter("username");
    String password = req.getParameter("password");

    int code = 0;

    UserDao userDao = new UserDao();
    User user = userDao.getByUsername(username);
    if (user == null) {
      code = 1; // Username not found.
    } else if (!user.isValidPassword(password)) {
      code = 2; // Invalid password.
    }
    Map<String, Object> data = Maps.newHashMap();
    data.put("code", code);
    if (code == 0) {
      Session session = new SessionDao().save(user);
      data.put("sid", session.getId());
    }
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(new Gson().toJson(data));
  }
}
