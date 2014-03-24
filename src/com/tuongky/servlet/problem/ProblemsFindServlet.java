package com.tuongky.servlet.problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.CounterDao;
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.ProblemUserMetadataDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.Session;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

/**
 * Find a list of problems and associated info with the user.
 *
 * Created by sngo on 2/12/14.
 */
@SuppressWarnings("serial")
public class ProblemsFindServlet extends HttpServlet {

  private static final String PAGE_NUM_FIELD = "page_num";
  private static final String PAGE_SIZE_FIELD = "page_size";
  private static final String ORDER_FIELD = "order";

  private static final String ROOT_KEY = "problem_search";
  private static final String TOTAL_RESULT = "total";

  private static int PAGE_SIZE_DEFAULT = 10;

  private Set<Long> getProblemIds(List<Problem> problemns) {
    Set<Long> ids = new HashSet<>();
    for (Problem problem : problemns) {
      ids.add(problem.getId());
    }
    return ids;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String pageNum = req.getParameter(PAGE_NUM_FIELD);
    String pageSize = req.getParameter(PAGE_SIZE_FIELD);
    String order = req.getParameter(ORDER_FIELD);

    resp.setContentType(Constants.CT_JSON_UTF8);

    try{
      int page = Integer.parseInt(pageNum);
      int size = PAGE_SIZE_DEFAULT;

      if (pageSize != null && !pageSize.isEmpty()) {
        try {
          size = Integer.parseInt(pageSize);
        }
        catch (NumberFormatException e){
          resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
          return;
        }
      }

      int startIndex = page * size;
      long totalResults = CounterDao.getProblemsCount();

      List<Problem> problems = ProblemDao.instance.search(startIndex, size, order);

      Map<String, Object> data = Maps.newHashMap();

      data.put(ROOT_KEY, problems);
      data.put(TOTAL_RESULT, totalResults);

      Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);

      List<Boolean> solved = Lists.newArrayList();
      Map<Long, Integer> problemMap = Maps.newHashMap();

      if (session != null) {
        solved = SolutionDao.instance.solvedByProblems(session.getUserId(), problems);
        problemMap = ProblemUserMetadataDao.instance.findAttemptsByUser(
            session.getUserId(), getProblemIds(problems));
      } else {
        for (int i = 0; i < problems.size(); i++) {
          solved.add(false);
        }
      }

      List<ResponseObject> responseObjects = new ArrayList<>();

      Iterator<Boolean> solvedIterator = solved.iterator();

      for (Problem problem : problems) {
        ResponseObject object;
        if (session != null) {
          object = new ResponseObject(
              problem,
              solvedIterator.next(),
              problemMap.containsKey(problem.getId()) ? problemMap.get(problem.getId()) : 0);
        } else {
          object = new ResponseObject(problem, solvedIterator.next(), 0);
        }
        responseObjects.add(object);
      }

      data.put(ROOT_KEY, responseObjects);
      data.put(TOTAL_RESULT, totalResults);

      resp.getWriter().println(new Gson().toJson(data));
    } catch (NumberFormatException e){
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
    }
  }

  @SuppressWarnings("unused") // Used by Gson.
  private static class ResponseObject{
    private final Problem problem;
    private final boolean isSolved;
    private final int attempts;

    public ResponseObject(Problem problem, boolean isSolved, int attempts){
      this.problem = problem;
      this.isSolved = isSolved;
      this.attempts = attempts;
    }
  }
}
