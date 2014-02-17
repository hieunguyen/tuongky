package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.model.datastore.ProblemAttempt;
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

  private static final String ATTEMPT_ID_FIELD = "attemptId";
  private static final String ROOT_KEY = "solved";

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String attemptId = req.getParameter(ATTEMPT_ID_FIELD);

    ProblemAttempt attempt = ProblemAttemptDao.instance.getById(attemptId);

    if (attempt != null){
      String id = SolutionDao.instance.solve(attempt.getActorId(), attempt.getProblemId());
      ProblemAttemptDao.instance.setAttemptStatus(attemptId, true);
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, id));
    }
    else {
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "fail"));
    }
  }
}
