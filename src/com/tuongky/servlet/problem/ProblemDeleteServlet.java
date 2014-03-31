package com.tuongky.servlet.problem;

import javax.servlet.http.HttpServlet;

import com.tuongky.backend.DatastoreUpdateService;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;
import com.tuongky.util.ValidationUtils;

/**
 * Created by sngo on 2/12/14.
 */
@SuppressWarnings("serial")
public class ProblemDeleteServlet extends HttpServlet {

  private static final String ID_FIELD = "id";
  private static final String ROOT_KEY = "problemDelete";

  @Override
  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    Long problemId = ValidationUtils.mustBeLong(req, resp, ID_FIELD);
    if (problemId == null) {
      return;
    }
    DatastoreUpdateService.instance.deleteProblem(problemId);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "ok"));
  }
}
