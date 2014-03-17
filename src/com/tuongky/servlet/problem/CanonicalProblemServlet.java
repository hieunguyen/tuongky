package com.tuongky.servlet.problem;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.tuongky.backend.ProblemDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.servlet.Constants;

@SuppressWarnings("serial")
public class CanonicalProblemServlet extends HttpServlet {

  private static final String FB_AGENT_IDENTIFIER = "facebookexternalhit";

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String problemId = extractProblemId(req);
    if (problemId == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Problem not found.");
      return;
    }
    if (!isFacebookBot(req)) {
      resp.sendRedirect("/#/problem/" + problemId);
    }
    long problemIdLong;
    try {
      problemIdLong = Long.parseLong(problemId);
    } catch (NumberFormatException e) {
      resp.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "problemId must be a long number. Got: " + problemId);
      return;
    }

    Problem problem = ProblemDao.instance.getById(problemIdLong);
    if (problem == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no problem with id " + problemId);
      return;
    }

    Map<String, Object> scopes = Maps.newHashMap();
    scopes.put("baseUrl", getBaseUrl(req));
    scopes.put("problemId", problemId);
    scopes.put("problemName", createProblemName(problem));
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("templates/canonical_problem.html");
    resp.setContentType(Constants.CT_HTML_UTF8);
    PrintWriter writer = resp.getWriter();
    mustache.execute(writer, scopes);
    writer.flush();
  }

  private String getBaseUrl(HttpServletRequest req) {
    StringBuffer url = req.getRequestURL();
    String uri = req.getRequestURI();
    String ctx = req.getContextPath();
    return url.substring(0, url.length() - uri.length() + ctx.length());
  }

  private String createProblemName(Problem problem) {
    String simpleName = "Bài " + problem.getId();
    if (Strings.isNullOrEmpty(problem.getTitle())) {
      return simpleName;
    }
    return simpleName + " - " + problem.getTitle();
  }

  private String extractProblemId(HttpServletRequest req) {
    String pathInfo = req.getPathInfo();
    if (pathInfo == null || pathInfo.length() < 2) {
      return null;
    }
    try {
      int problemId = Integer.parseInt(pathInfo.substring(1));
      return String.valueOf(problemId);
    } catch(NumberFormatException e) {
      return null;
    }
  }

  private boolean isFacebookBot(HttpServletRequest req) {
    String userAgent = req.getHeader("user-agent");
    return userAgent != null && userAgent.indexOf(FB_AGENT_IDENTIFIER) >= 0;
  }
}
