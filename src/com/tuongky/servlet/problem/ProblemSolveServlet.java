package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Created by sngo on 2/10/14.
 */
@SuppressWarnings("serial")
public class ProblemSolveServlet extends HttpServlet {

  private static final String ACTOR_ID_FIELD = "actorId";
  private static final String PROBLEM_ID_FIELD = "problemId";
  private static final String SUCCESS_FIELD = "isSuccess";
  private static final String ROOT_KEY = "solved";

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String actorId = req.getParameter(ACTOR_ID_FIELD);
    String problemId = req.getParameter(PROBLEM_ID_FIELD);
    String isSuccess = req.getParameter(SUCCESS_FIELD);

    long actor = Long.parseLong(actorId);
    long problem = Long.parseLong(problemId);

    if ("on".equals(isSuccess)){
      SolutionDao.instance.solve(actor, problem);
      ProblemAttemptDao.instance.attempt(actor, problem, true);
    }else{
      ProblemAttemptDao.instance.attempt(actor, problem, false);
    }

    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "ok"));
  }
}
