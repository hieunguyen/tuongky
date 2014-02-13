package com.tuongky.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.tuongky.backend.GameMetadataDao;
import com.tuongky.model.GameQuery;
import com.tuongky.model.GameSearchResult;
import com.tuongky.model.datastore.Game;
import com.tuongky.model.datastore.GameMetadata;
import com.tuongky.service.SearchService;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameSearchServlet extends HttpServlet {

  private List<GameMetadata> getGameMetadatas(List<Game> games) {
    GameMetadataDao gameMetadataDao = new GameMetadataDao();
    List<String> ids = Lists.newArrayList();
    for (Game game : games) {
      ids.add(game.getId());
    }
    return gameMetadataDao.getByIds(ids);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String queryString = req.getParameter("q");
    String category = req.getParameter("category");
    String title = req.getParameter("title");
    String book = req.getParameter("book");
    String start = req.getParameter("start");
    int offset = 0;
    if (start != null) {
      offset = Integer.parseInt(start);
    }
    GameSearchResult gameSearchResult = SearchService.search(
        new GameQuery(queryString, category, title, book, offset));
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson(
        "games", gameSearchResult.getGames(),
        "gameMetadatas", getGameMetadatas(gameSearchResult.getGames()),
        "numberFound", gameSearchResult.getNumberFound()
    ));
  }
}
