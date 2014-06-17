package com.tuongky.servlet.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.User;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;

@SuppressWarnings("serial")
public class UserEmailListServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!AuthUtils.allowAdminOnly(req, resp)) {
      return;
    }
    List<User> users = UserDao.instance.getAll();
    StringBuffer output = new StringBuffer();
    for (User user : users) {
      if (Strings.isNullOrEmpty(user.getFbId()) || Strings.isNullOrEmpty(user.getEmail())) {
        continue;
      }
      String userInfo = user.getEmail() + ", " + user.getFbName();
      output.append(userInfo + "\n");
    }
    resp.setContentType(Constants.CT_PLAIN_UTF8);
    resp.getWriter().println(output.toString());
  }
}
