package com.tuongky.servlet.user;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.CounterDao;
import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.backend.UserDao;
import com.tuongky.backend.UserMetadataDao;
import com.tuongky.backend.UserRankerDao;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;

@SuppressWarnings("serial")
public class UserProfileServlet extends HttpServlet {
  private static final String ID_FIELD = "fb_id";

  private static final String USER_KEY = "user";
  private static final String METADATA_KEY = "metadata";
  private static final String SOLVE_KEY = "solve";
  private static final String ATTEMPT_KEY = "attempt";

  private RankInfo getRankInfo(UserMetadata userMetadata){
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
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String fbId = req.getParameter(ID_FIELD);
    if (fbId == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    User user = UserDao.instance.getByFbId(fbId);
    if (user == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    UserMetadata metadata = UserMetadataDao.instance.get(user.getId());

    Map<String, Object> ret = Maps.newHashMap();

    ret.put(USER_KEY, user);
    ret.put(METADATA_KEY, metadata);

    List<Solution> solutionList = SolutionDao.instance.searchByActor(
        user.getId(), Integer.MAX_VALUE, 0);
    ret.put(SOLVE_KEY, solutionList);

    List<ProblemAttempt> attempts = ProblemAttemptDao.instance.searchByActor(
        user.getId(), false, Integer.MAX_VALUE, 0);
    ret.put(ATTEMPT_KEY, attempts);

    ret.put("rankInfo", getRankInfo(metadata));

    ret.put("problemCount", CounterDao.getProblemsCount());

    Session session = AuthUtils.getSession(req);
    if (session != null && session.getUserId() == user.getId()) {
      List<ProblemAttempt> lastFailedAttempts =
          ProblemAttemptDao.instance.findLastFailedAttempts(user.getId());
      ret.put("failedAttempts", lastFailedAttempts);
    }

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(new Gson().toJson(ret));
  }
}
