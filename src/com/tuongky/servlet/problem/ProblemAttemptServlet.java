package com.tuongky.servlet.problem;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.DatastoreUpdateService;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Session;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

/**
 * This servlet is called when user starts solving a problem.
 * When the attempt is successful, ProblemSolveServlet will be called. Otherwise, do nothing
 *
 * Created by sngo on 2/14/14.
 */
@SuppressWarnings("serial")
public class ProblemAttemptServlet extends HttpServlet {
  private static final String PROBLEM_ID_FIELD = "problem_id";
  private static final String ROOT_KEY = "attemptId";

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
    if (session == null) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    long userId = session.getUserId();
    String problemId = req.getParameter(PROBLEM_ID_FIELD);
    long problem = Long.parseLong(problemId);
    ProblemAttempt attempt = DatastoreUpdateService.instance.attemptProblem(userId, problem);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, attempt.getId()));
  }
}
