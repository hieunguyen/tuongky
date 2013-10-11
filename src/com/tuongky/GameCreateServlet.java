package com.tuongky;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.tuongky.backend.GameDao;
import com.tuongky.model.GameCategory;
import com.tuongky.model.datastore.Game;
import com.tuongky.service.SearchService;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameCreateServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    int categoryIndex = Integer.parseInt(req.getParameter("category"));
    GameCategory category = GameCategory.fromValue(categoryIndex);
    Preconditions.checkState(!category.isUnKnown(), "Unknown game category.");
    String title = req.getParameter("title");
    String book = req.getParameter("book");
    String data = req.getParameter("data");
    String username = req.getParameter("username");
    GameDao gameDao = new GameDao();
    Game gameData = gameDao.save(username, category, title, book, data);
    SearchService.indexGame(gameData);
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(JsonUtils.toJson("status", "ok"));
  }
}
