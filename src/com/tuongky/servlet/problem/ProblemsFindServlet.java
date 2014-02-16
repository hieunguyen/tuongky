package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;

import java.util.List;

/**
 * Created by sngo on 2/12/14.
 */
@SuppressWarnings("serial")
public class ProblemsFindServlet extends HttpServlet{

  private static final String PAGE_NUM_FIELD = "pageNum";
  private static final String PAGE_SIZE_FIELD = "pageSize";
  private static final String ORDER_FIELD = "order";

  private static final String ROOT_KEY = "problemSearch";

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

      List<Problem> list = ProblemDao.instance.search(startIndex, size, order);

      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, list));
    } catch (NumberFormatException e){
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
    }
  }

}
