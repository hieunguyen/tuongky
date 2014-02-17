package com.tuongky.servlet.problem;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.Maps;
import com.tuongky.servlet.Constants;

@SuppressWarnings("serial")
public class CanonicalProblemServlet extends HttpServlet {

  private static final String FB_AGENT_IDENTIFIER = "facebookexternalhit";

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!isFacebookBot(req)) {
      resp.sendRedirect("/#/problem/2");
    }
    HashMap<String, Object> scopes = Maps.newHashMap();
    scopes.put("name", "Mustache");
    scopes.put("version", "1.0");
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("templates/test.html");
    resp.setContentType(Constants.CT_HTML_UTF8);
    PrintWriter writer = resp.getWriter();
    mustache.execute(writer, scopes);
    writer.flush();
  }

  private boolean isFacebookBot(HttpServletRequest req) {
    String userAgent = req.getHeader("user-agent");
    return userAgent != null && userAgent.indexOf(FB_AGENT_IDENTIFIER) >= 0;
  }
}
