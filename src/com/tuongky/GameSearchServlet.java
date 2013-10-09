package com.tuongky;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.model.GameQuery;
import com.tuongky.model.GameSearchResult;
import com.tuongky.service.SearchService;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameSearchServlet extends HttpServlet {

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
        "numberFound", gameSearchResult.getNumberFound()
    ));
  }
}
