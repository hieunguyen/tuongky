package com.tuongky.servlet.problem;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.DatastoreUpdateService;
import com.tuongky.logic.FenParser;
import com.tuongky.model.datastore.Problem;
import com.tuongky.servlet.Constants;
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
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
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

    Problem problem = new Problem(null, title, fen, description, requirement, creatorId);
    problem = DatastoreUpdateService.instance.createProblem(problem);

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, problem.getId()));
  }
}
