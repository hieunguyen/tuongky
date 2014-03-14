package com.tuongky.servlet.game;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.tuongky.backend.BookDao;
import com.tuongky.backend.GameDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.GameCategory;
import com.tuongky.model.datastore.Game;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.User;
import com.tuongky.service.SearchService;
import com.tuongky.servlet.Constants;
import com.tuongky.util.AuthUtils;
import com.tuongky.util.JsonUtils;
import com.tuongky.util.ValidationUtils;

@SuppressWarnings("serial")
public class GameSaveServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Session session = AuthUtils.mustHaveSession(req, resp);
    if (session == null) {
      return;
    }

    Integer categoryIndex = ValidationUtils.mustBeInt(req, resp, "category");
    if (categoryIndex == null) {
      return;
    }

    GameCategory category = GameCategory.fromValue(categoryIndex);
    if (category.isUnKnown()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown game category.");
      return;
    }

    String title = ValidationUtils.mustBeSet(req, resp, "title");
    if (title == null) {
      return;
    }

    String nTitle = ValidationUtils.mustBeSet(req, resp, "n_title");
    if (nTitle == null) {
      return;
    }

    String data = ValidationUtils.mustBeSet(req, resp, "data");
    if (data == null) {
      return;
    }

    String id = req.getParameter("id");
    String book = req.getParameter("book");
    String nBook = req.getParameter("n_book");

    User user = UserDao.instance.getById(session.getUserId());

    String oldBook = req.getParameter("old_book");
    if ((oldBook == null || Integer.parseInt(oldBook) == 0) && !Strings.isNullOrEmpty(book)) {
      BookDao bookDao = new BookDao();
      bookDao.save(user.getUsername(), user.getFbId(), book);
    }

    Game gameData;
    if (id == null) {
      gameData = GameDao.instance.save(user.getUsername(), user.getFbId(), user.getFbName(),
          category, title, nTitle, book, nBook, data);
    } else {
      Game game = GameDao.instance.getById(id);
      if (game == null) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
            String.format("Game with ID \"%s\" does not exist.", id));
        return;
      }
      if (!isCreator(user, game)) {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You cannot edit this game.");
        return;
      }
      gameData = GameDao.instance.save(id, user.getUsername(), user.getFbId(), user.getFbName(),
          category, title, nTitle, book, nBook, data);
    }

    SearchService.indexGame(gameData);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson("gameId", gameData.getId()));
  }

  private boolean isCreator(User user, Game game) {
    if (user.getUsername() != null && game.getUsername() != null) {
      return user.getUsername().equalsIgnoreCase(game.getUsername());
    }
    if (user.getFbId() != null && game.getFbId() != null) {
      return user.getFbId().equalsIgnoreCase(game.getFbId());
    }
    return false;
  }
}
