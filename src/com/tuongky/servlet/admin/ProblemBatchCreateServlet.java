package com.tuongky.servlet.admin;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import com.google.appengine.labs.repackaged.com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.tuongky.backend.DatastoreUpdateService;
import com.tuongky.logic.FenParser;
import com.tuongky.model.datastore.Problem;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;
import com.tuongky.util.JsonUtils;
import com.tuongky.util.ValidationUtils;

@SuppressWarnings("serial")
public class ProblemBatchCreateServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(
      ProblemBatchCreateServlet.class.getName());

  private List<String> toValidFens(String fensField) {
    List<String> fens = Lists.newArrayList();
    for (String fen : Splitter.on('\n').split(fensField)) {
      if (FenParser.isValidFen(fen.trim())) {
        fens.add(fen);
      }
    }
    return fens;
  }

  private List<Long> createProblems(long creatorId, List<String> fens) {
    List<Long> problemIds = Lists.newArrayList();
    for (String fen : fens) {
      Problem problem = new Problem(null, null, fen, null, null, null);
      DatastoreUpdateService.instance.createProblem(problem);
      problemIds.add(problem.getId());
    }
    return problemIds;
  }

  @Override
  public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    if (!AuthUtils.allowAdminOnly(req, resp)) {
      return;
    }

    String fensField = ValidationUtils.mustBeSet(req, resp, "fens");
    if (fensField == null) {
      return;
    }

    List<String> fens = toValidFens(fensField);
    logger.info("Found " + fens.size() + " valid fens.");

    long creatorId = AuthUtils.getSession(req).getUserId();

    List<Long> problemIds = createProblems(creatorId, fens);
    logger.info("Created" + problemIds.size() + " new problems.");

    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson("problemIds", problemIds));
  }
}
