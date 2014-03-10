package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Session;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Only call when an attempt is successful.
 *
 * Created by sngo on 2/10/14.
 */
@SuppressWarnings("serial")
public class ProblemSolveServlet extends HttpServlet {

  private static final String ATTEMPT_ID_FIELD = "attempt_id";
  private static final String ROOT_KEY = "solutionId";

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String attemptId = req.getParameter(ATTEMPT_ID_FIELD);

    if (attemptId == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "attemptId is required.");
      return;
    }
    ProblemAttempt attempt = ProblemAttemptDao.instance.getById(attemptId);

    if (attempt == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Attempt not found.");
      return;
    }

    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
    if (session == null) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    if (session.getUserId() != attempt.getActorId()) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not the attempter.");
      return;
    }

    String id = SolutionDao.instance.solve(attempt.getActorId(), attempt.getProblemId());
    ProblemAttemptDao.instance.setAttemptStatus(attemptId, true);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, id));
  }
}
