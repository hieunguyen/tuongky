package com.tuongky.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;

@SuppressWarnings("serial")
public class AdminMainServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!AuthUtils.allowAdminOnly(req, resp)) {
      return;
    }
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("templates/admin_main.html");
    resp.setContentType(Constants.CT_HTML_UTF8);
    PrintWriter writer = resp.getWriter();
    mustache.execute(writer, new HashMap<String, Object>());
    writer.flush();
  }
}
