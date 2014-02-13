package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemDao;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;

/**
 * Created by sngo on 2/12/14.
 */
public class ProblemDeleteServlet extends HttpServlet {

  private static final String ROOT_KEY = "problemDelete";

  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {

    String id = req.getParameter(ProblemServlet.ID_FIELD);

    long idLong = Long.parseLong(id);

    ProblemDao.instance.delete(idLong);

    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "ok"));
  }

}
