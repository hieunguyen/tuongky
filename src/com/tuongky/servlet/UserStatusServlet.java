package com.tuongky.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.User;

@SuppressWarnings("serial")
public class UserStatusServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
    User user = null;
    if (session != null) {
      user = new UserDao().getById(session.getUserId());
    }
    Map<String, Object> data = Maps.newHashMap();
    if (user != null) {
      data.put("fbId", user.getFbId());
      data.put("fbName", user.getFbName());
      data.put("roleId", user.getRoleIndex());
    }
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(new Gson().toJson(data));
  }
}
