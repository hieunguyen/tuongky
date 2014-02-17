package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet is called when user starts solving a problem.
 * When the attempt is successful, ProblemSolveServlet will be called. Otherwise, do nothing
 *
 * Created by sngo on 2/14/14.
 */
public class ProblemAttemptServlet extends HttpServlet {
  private static final String ACTOR_ID_FIELD = "actorId";
  private static final String PROBLEM_ID_FIELD = "problemId";
  private static final String ROOT_KEY = "attempt";

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String actorId = req.getParameter(ACTOR_ID_FIELD);
    String problemId = req.getParameter(PROBLEM_ID_FIELD);

    long actor = Long.parseLong(actorId);
    long problem = Long.parseLong(problemId);

    String attemptId = ProblemAttemptDao.instance.attempt(actor, problem, false);

    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, attemptId));
  }
}
