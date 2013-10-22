package com.tuongky;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.InviteDao;
import com.tuongky.model.datastore.Invite;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class InviteCreateServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    InviteDao inviteDao = new InviteDao();
    Invite invite = inviteDao.save();
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(JsonUtils.toJson("inviteId", invite.getId()));
  }
}
