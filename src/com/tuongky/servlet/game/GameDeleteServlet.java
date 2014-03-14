package com.tuongky.servlet.game;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.GameDao;
import com.tuongky.model.datastore.Game;
import com.tuongky.service.SearchService;
import com.tuongky.servlet.Constants;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameDeleteServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String id = req.getParameter("id");
    String username = req.getParameter("username");
    GameDao gameDao = new GameDao();
    Game game = gameDao.getById(id);
    if (game != null && !game.getUsername().equals(username)) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You cannot delete this game.");
      return;
    }
    gameDao.delete(id);
    SearchService.deleteGame(id);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson("status", "ok"));
  }

}
