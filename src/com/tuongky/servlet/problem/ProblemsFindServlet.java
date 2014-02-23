package com.tuongky.servlet.problem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tuongky.backend.CounterDao;
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.SolutionDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.Session;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

/**
 * Created by sngo on 2/12/14.
 */
@SuppressWarnings("serial")
public class ProblemsFindServlet extends HttpServlet{

  private static final String PAGE_NUM_FIELD = "pageNum";
  private static final String PAGE_SIZE_FIELD = "pageSize";
  private static final String ORDER_FIELD = "order";

  private static final String ROOT_KEY = "problemSearch";
  private static final String TOTAL_RESULT = "total";
  private static final String SOLVED = "solved";

  private static int PAGE_SIZE_DEFAULT = 10;

  @Override
  public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String pageNum = req.getParameter(PAGE_NUM_FIELD);
    String pageSize = req.getParameter(PAGE_SIZE_FIELD);
    String order = req.getParameter(ORDER_FIELD);

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
      long totalPages = (long)Math.ceil((double)CounterDao.getProblemsCount()/size);

      List<Problem> problems = ProblemDao.instance.search(startIndex, size, order);

      Map<String, Object> data = Maps.newHashMap();

      data.put(ROOT_KEY, problems);
      data.put(TOTAL_RESULT, totalPages);

      Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
      if (session != null) {
        List<Boolean> solved = SolutionDao.instance.solvedByProblems(session.getUserId(), problems);
        data.put(SOLVED, solved);
      }

      resp.getWriter().println(new Gson().toJson(data));
    } catch (NumberFormatException e){
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
    }
  }
}
