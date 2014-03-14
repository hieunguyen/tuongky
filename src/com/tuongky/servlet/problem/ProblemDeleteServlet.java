package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemDao;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;

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

    String id = req.getParameter(ID_FIELD);

    long idLong = Long.parseLong(id);

    ProblemDao.instance.delete(idLong);

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "ok"));
  }

}
