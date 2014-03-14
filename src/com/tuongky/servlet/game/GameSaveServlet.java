package com.tuongky.servlet.game;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.tuongky.backend.BookDao;
import com.tuongky.backend.GameDao;
import com.tuongky.model.GameCategory;
import com.tuongky.model.datastore.Game;
import com.tuongky.service.SearchService;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameSaveServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    int categoryIndex = Integer.parseInt(req.getParameter("category"));
    GameCategory category = GameCategory.fromValue(categoryIndex);
    Preconditions.checkState(!category.isUnKnown(), "Unknown game category.");
    String id = req.getParameter("id");
    String username = req.getParameter("username");
    String title = req.getParameter("title");
    String nTitle = req.getParameter("n_title");
    String book = req.getParameter("book");
    String nBook = req.getParameter("n_book");
    String data = req.getParameter("data");

    String oldBook = req.getParameter("old_book");
    if ((oldBook == null || Integer.parseInt(oldBook) == 0) && !Strings.isNullOrEmpty(book)) {
      BookDao bookDao = new BookDao();
      bookDao.save(username, book);
    }

    GameDao gameDao = new GameDao();
    Game gameData = gameDao.save(id, username, category, title, nTitle, book, nBook, data);
    SearchService.indexGame(gameData);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson("status", "ok"));
  }
}
