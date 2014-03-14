package com.tuongky.servlet.problem;

import java.util.List;

import javax.servlet.http.HttpServlet;

import com.tuongky.backend.ProblemAttemptDao;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

/**
 * Created by sngo on 2/13/14.
 */
@SuppressWarnings("serial")
public class LastAttemptsServlet extends HttpServlet{
  private static final String PAGE_SIZE_FIELD = "pageSize";

  private static final String ROOT_KEY = "lastAttempts";

  private static int PAGE_SIZE_DEFAULT = 20;

  @Override
  public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String pageSize = req.getParameter(PAGE_SIZE_FIELD);

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

    List<ProblemAttempt> list = ProblemAttemptDao.instance.find(0, size);

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, list));
  }
}
