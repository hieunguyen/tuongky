package com.tuongky.servlet.problem;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.ProblemDao;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.Session;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;
import com.tuongky.util.JsonUtils;
import com.tuongky.util.ValidationUtils;

@SuppressWarnings("serial")
public class ProblemGetNextServlet extends HttpServlet {

  private static final String ID_FIELD = "id";
  private static final String NEXT_ID_FIELD = "nextId";

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Long problemId = ValidationUtils.mustBeLong(req, resp, ID_FIELD);
    if (problemId == null) {
      return;
    }
    Session session = AuthUtils.getSession(req);
    Long userId = session == null ? null : session.getUserId();
    Problem nextProblem = ProblemDao.instance.getNextUnsolved(userId, problemId);
    long nextId = nextProblem == null ? -1 : nextProblem.getId();
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(NEXT_ID_FIELD, nextId));
  }
}
