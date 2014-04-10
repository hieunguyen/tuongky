package com.tuongky.servlet.problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.ProblemMetadataDao;
import com.tuongky.backend.ProblemUserMetadataDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.ProblemMetadata;
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;
import com.tuongky.model.response.SolutionResponse;
import com.tuongky.servlet.Constants;
import com.tuongky.util.ValidationUtils;

/**
 * Created by sngo on 2/13/14.
 */
@SuppressWarnings("serial")
public class ProblemGetServlet extends HttpServlet {
  private static final String ID_FIELD = "id";
  private static final String SOLVE_INCLUDED = "solve_included";
  private static final String ATTEMPT_INCLUDED = "attempt_included";

  private static final String ROOT_KEY = "problem";
  private static final String SOLVE = "solve";
  private static final String ATTEMPT = "attempt";
  private static final String ATTEMPT_COUNT = "attempt_count";
  private static final String SOLVED = "solved";
  private static final String NEXT_PROBLEM = "next_problem";
  private static final String IS_ON = "on";

  private Set<Long> getUserIdSet(List<Solution> solutions){
    Set<Long> ret = new HashSet<>();
    for (Solution solution : solutions){
      ret.add(solution.getActorId());
    }

    return ret;
  }

  private void recordView(long problemId) {
    ProblemMetadata problemMetadata = ProblemMetadataDao.instance.getById(problemId);
    if (problemMetadata == null) {
      problemMetadata = new ProblemMetadata(problemId);
    }
    problemMetadata.view();
    ProblemMetadataDao.instance.save(problemMetadata);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    Long problemId = ValidationUtils.mustBeLong(req, resp, ID_FIELD);
    if (problemId == null) {
      return;
    }

    String solvers = req.getParameter(SOLVE_INCLUDED);
    String attempters = req.getParameter(ATTEMPT_INCLUDED);

    boolean solveIncluded = IS_ON.equals(solvers);
    boolean attemptIncluded = IS_ON.equals(attempters);

    Problem problem = ProblemDao.instance.getById(problemId);

    if (problem == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no problem with id " + problemId);
      return;
    }

    recordView(problem.getId());

    Map<String, Object> ret = Maps.newHashMap();

    ret.put(ROOT_KEY, problem);

    if (solveIncluded) {
      List<Solution> solutionList =
          SolutionDao.instance.searchByProblem(problemId, Integer.MAX_VALUE, 0);

      Set<Long> userIdSet = getUserIdSet(solutionList);
      Map<Long, Integer> userMap =
          ProblemUserMetadataDao.instance.findAttemptsByProblem(problemId, userIdSet);

      List<ResponseObject> responseObjects = new ArrayList<>();
      for (Solution solution : solutionList) {
        SolutionResponse solutionResponse = SolutionResponse.fromSolution(solution);
        if (userMap.containsKey(solution.getActorId())) {
          responseObjects.add(new ResponseObject(
              solutionResponse, userMap.get(solution.getActorId())));
        }else {
          responseObjects.add(new ResponseObject(solutionResponse, 1));
        }
      }

      ret.put(SOLVE, responseObjects);
    }

    if (attemptIncluded) {
      List<ProblemAttempt> attempts = ProblemAttemptDao.instance.searchByProblem(
          problemId, false, Integer.MAX_VALUE, 0);
      ret.put(ATTEMPT, attempts);
    }

    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);

    Long userId = null;
    if (session != null) {
      User user = UserDao.instance.getById(session.getUserId());
      boolean solved = SolutionDao.instance.solvedByProblem(session.getUserId(), problem);
      ret.put(SOLVED, solved);
      ProblemUserMetadata metadata =
          ProblemUserMetadataDao.instance.getByUserAndProblem(user, problemId);
      ret.put(ATTEMPT_COUNT, metadata == null ? 0 : metadata.getAttempts());
      userId = session.getUserId();
    }

    Problem nextProblem = ProblemDao.instance.getNextUnsolved(userId, problemId);
    ret.put(NEXT_PROBLEM, nextProblem);

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(new Gson().toJson(ret));
  }

  @SuppressWarnings("unused") // Used by Gson.
  private static class ResponseObject {
    private final SolutionResponse solution;
    private final int attempts;

    public ResponseObject(SolutionResponse solution, int attempts) {
      this.solution = solution;
      this.attempts = attempts;
    }
  }
}
