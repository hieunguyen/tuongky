package com.tuongky.servlet.admin;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.util.AuthUtils;

@SuppressWarnings("serial")
public class ResetAllServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!AuthUtils.allowAdminOnly(req, resp)) {
      return;
    }
  }
}
