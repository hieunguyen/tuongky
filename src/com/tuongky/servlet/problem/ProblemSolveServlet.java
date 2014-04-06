package com.tuongky.servlet.problem;

import com.tuongky.backend.DatastoreUpdateService;
import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.Solution;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;
import com.tuongky.util.JsonUtils;
import com.tuongky.util.ValidationUtils;

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
    Session session = AuthUtils.mustHaveSession(req, resp);
    if (session == null) {
      return;
    }

    String jsonData = ValidationUtils.mustBeSet(req, resp, "json_data");
    if (jsonData == null) {
      return;
    }

    String attemptId = ValidationUtils.mustBeSet(req, resp, ATTEMPT_ID_FIELD);
    if (attemptId == null) {
      return;
    }

    ProblemAttempt attempt = ProblemAttemptDao.instance.getById(session.getUserId(), attemptId);
    if (attempt == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Attempt not found.");
      return;
    }

    if (session.getUserId() != attempt.getActorId()) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not the attempter.");
      return;
    }

    Solution solution = DatastoreUpdateService.instance.solveProblem(attempt, jsonData);

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, solution.getId()));
  }
}
