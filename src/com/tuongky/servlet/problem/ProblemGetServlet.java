package com.tuongky.servlet.problem;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.Solution;
import com.tuongky.servlet.Constants;

import javax.servlet.http.HttpServlet;

import java.util.List;
import java.util.Map;

/**
 * Created by sngo on 2/13/14.
 */
@SuppressWarnings("serial")
public class ProblemGetServlet extends HttpServlet {
  private static final String ID_FIELD = "id";
  private static final String SOLVE_INCLUDED = "solveIncluded";
  private static final String ATTEMPT_INCLUDED = "attemptIncluded";

  private static final String ROOT_KEY = "problem";
  private static final String SOLVE = "solve";
  private static final String ATTEMPT = "attempt";
  private static final String SOLVED = "solved";
  private static final String IS_ON = "on";

  @Override
  public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String id = req.getParameter(ID_FIELD);
    String solvers = req.getParameter(SOLVE_INCLUDED);
    String attempters = req.getParameter(ATTEMPT_INCLUDED);

    boolean solveIncluded = IS_ON.equals(solvers);
    boolean attemptIncluded = IS_ON.equals(attempters);

    long idLong = Long.parseLong(id);
    Problem problem = ProblemDao.instance.getById(idLong);

    Map<String, Object> ret = Maps.newHashMap();

    ret.put(ROOT_KEY, problem);

    if (solveIncluded){
      List<Solution> solutionList = SolutionDao.instance.searchByProblem(idLong, Integer.MAX_VALUE, 0);
      ret.put(SOLVE, solutionList);
    }

    if (attemptIncluded){
      List<ProblemAttempt> attempts = ProblemAttemptDao.instance.searchByProblem(idLong, false, Integer.MAX_VALUE, 0);
      ret.put(ATTEMPT, attempts);
    }

    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
    if (session != null) {
      boolean solved = SolutionDao.instance.solvedByProblem(session.getUserId(), problem);
      ret.put(SOLVED, solved);
    }

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(new Gson().toJson(ret));
  }

}
