package com.tuongky.servlet.problem;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;
import com.tuongky.util.ValidationUtils;

@SuppressWarnings("serial")
public class SolutionsForProblemServlet extends HttpServlet {

  private static final String PROBLEM_ID_FIELD = "id";
  private static final String PAGE_NUM_FIELD = "page_num";
  private static final String PAGE_SIZE_FIELD = "page_size";
  private static int PAGE_NUM_DEFAULT = 0;
  private static int PAGE_SIZE_DEFAULT = 10;

  private static final String ITEMS_KEY = "items";
  private static final String PROBLEM_KEY = "problem";

  private static final Logger log = Logger.getLogger(SolutionsForProblemServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Long problemId = ValidationUtils.mustBeLong(req, resp, PROBLEM_ID_FIELD);
    if (problemId == null) {
      return;
    }

    Integer pageSize = ValidationUtils.mayBeInt(req, resp, PAGE_SIZE_FIELD, PAGE_SIZE_DEFAULT);
    if (pageSize == null) {
      return;
    }

    Integer pageNum = ValidationUtils.mayBeInt(req, resp, PAGE_NUM_FIELD, PAGE_NUM_DEFAULT);
    if (pageNum == null) {
      return;
    }

    List<Solution> solutions = SolutionDao.instance.searchByProblem(problemId, pageSize, pageNum);

    Set<Long> ids = extractUserIds(solutions);
    Map<Long, User> userMap = UserDao.instance.batchGetById(ids);

    if (userMap.size() != ids.size()) {
      log.severe("Data is inconsistent, asking for ids = " + ids + " got back: " + userMap.keySet());
    }

    Set<Long> includedUserIds = new HashSet<>();

    List<ResponseObject> items = Lists.newArrayList();
    for (Solution solution : solutions) {
      long userId = solution.getActorId();

      if (!includedUserIds.contains(userId)) {
        items.add(new ResponseObject(solution, userMap.get(userId)));
        includedUserIds.add(userId);
      }
    }

    Problem problem = ProblemDao.instance.getById(problemId);

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(
        ITEMS_KEY, items, PROBLEM_KEY, problem));
  }

  private Set<Long> extractUserIds(List<Solution> solutions) {
    Set<Long> userIds = Sets.newHashSet();
    for (Solution solution : solutions) {
      userIds.add(solution.getActorId());
    }
    return userIds;
  }

  @SuppressWarnings("unused") // Used by Gson.
  private static class ResponseObject{

    private final Solution solution;
    private final User user;

    public ResponseObject(Solution solution, User user) {
      this.solution = solution;
      this.user = user;
    }
  }
}
