package com.tuongky.servlet.problem;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.ProblemDao;
import com.tuongky.logic.FenParser;
import com.tuongky.util.AuthUtils;
import com.tuongky.util.JsonUtils;

/**
 * Created by sngo on 2/12/14.
 */
@SuppressWarnings("serial")
public class ProblemCreateServlet extends HttpServlet {

  private static final String FEN_FIELD = "fen";
  private static final String DESCRIPTION_FIELD = "description";
  private static final String TITLE_FIELD = "title";
  private static final String REQUIREMENT_FIELD = "requirement";
  private static final String ROOT_KEY = "problem";

  @Override
  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    if (!AuthUtils.allowAdminOnly(req, resp)) {
      return;
    }

    long creatorId = AuthUtils.getSession(req).getUserId();

    String fen = req.getParameter(FEN_FIELD);

    if (!FenParser.isValidFen(fen)) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid fen: " + fen);
      return;
    }

    String description = req.getParameter(DESCRIPTION_FIELD);
    String title = req.getParameter(TITLE_FIELD);
    String requirement = req.getParameter(REQUIREMENT_FIELD);

    long problemId = ProblemDao.instance.create(fen, title, description, requirement, creatorId);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, problemId));
  }
}
