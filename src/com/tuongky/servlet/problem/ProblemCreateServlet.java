package com.tuongky.servlet.problem;

import com.tuongky.backend.ProblemDao;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;

import java.util.logging.Logger;

/**
 * Created by sngo on 2/12/14.
 */
@SuppressWarnings("serial")
public class ProblemCreateServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(ProblemCreateServlet.class.getName());

  private static final String FEN_FIELD = "fen";
  private static final String DESCRIPTION_FIELD = "description";
  private static final String TITLE_FIELD = "title";
  private static final String REQUIREMENT_FIELD = "requirement";
  private static final String CREATOR_ID_FIELD = "creator_id";
  private static final String ROOT_KEY = "problem";

  @Override
  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String fen = req.getParameter(FEN_FIELD);
    String description = req.getParameter(DESCRIPTION_FIELD);
    String title = req.getParameter(TITLE_FIELD);
    String requirement = req.getParameter(REQUIREMENT_FIELD);

    String creatorId = req.getParameter(CREATOR_ID_FIELD);

    Long creatorLong = null;
    try {
      creatorLong = Long.parseLong(creatorId);
    }
    catch (Exception e)
    {
      log.severe("CreatorId is of wrong format: " + creatorId);
    }

    long problemId = ProblemDao.instance.create(fen, title, description, requirement, creatorLong);

    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, problemId));
  }

}
