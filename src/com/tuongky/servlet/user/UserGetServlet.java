package com.tuongky.servlet.user;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.*;
import com.tuongky.model.datastore.*;
import com.tuongky.servlet.Constants;

import javax.servlet.http.HttpServlet;

import java.util.List;
import java.util.Map;

/**
 * Created by sngo on 2/13/14.
 */
@SuppressWarnings("serial")
public class UserGetServlet extends HttpServlet {
  private static final String ID_FIELD = "id";
  private static final String SOLVE_INCLUDED = "solve_included";
  private static final String ATTEMPT_INCLUDED = "attempt_included";

  private static final String USER_KEY = "user";
  private static final String METADATA_KEY = "metadata";
  private static final String SOLVE_KEY = "solve";
  private static final String ATTEMPT_KEY = "attempt";
  private static final String IS_ON = "on";

  public static RankInfo getRankInfo(UserMetadata userMetadata){
    if (userMetadata != null && userMetadata.getSolves() > 0)
    {
      int more = UserRankerDao.instance.getRank(userMetadata.getSolves() + 1);
      int same = UserRankerDao.instance.getRank(userMetadata.getSolves());
      int total = UserRankerDao.instance.getRank(1);

      return new RankInfo(more + 1, same - more, total);
    }

    return null;
  }

  @Override
  public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String id = req.getParameter(ID_FIELD);
    String solvers = req.getParameter(SOLVE_INCLUDED);
    String attempters = req.getParameter(ATTEMPT_INCLUDED);

    boolean solveIncluded = IS_ON.equals(solvers);
    boolean attemptIncluded = IS_ON.equals(attempters);

    long idLong = Long.parseLong(id);
    User user = UserDao.instance.getById(idLong);
    UserMetadata metadata = UserMetadataDao.instance.get(idLong);

    Map<String, Object> ret = Maps.newHashMap();

    ret.put(USER_KEY, user);
    ret.put(METADATA_KEY, metadata);

    if (solveIncluded){
      List<Solution> solutionList = SolutionDao.instance.searchByActor(idLong, Integer.MAX_VALUE, 0);
      ret.put(SOLVE_KEY, solutionList);
    }

    if (attemptIncluded){
      List<ProblemAttempt> attempts = ProblemAttemptDao.instance.searchByActor(idLong, false, Integer.MAX_VALUE, 0);
      ret.put(ATTEMPT_KEY, attempts);
    }

    ret.put("rankInfo", getRankInfo(metadata));

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(new Gson().toJson(ret));
  }
}
