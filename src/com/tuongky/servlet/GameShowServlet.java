package com.tuongky.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.tuongky.backend.GameDao;
import com.tuongky.backend.GameMetadataDao;
import com.tuongky.model.datastore.Game;
import com.tuongky.model.datastore.GameMetadata;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameShowServlet extends HttpServlet {

  private void recordView(String gameId) {
    GameMetadataDao gameMetadataDao = new GameMetadataDao();
    GameMetadata gameMetadata = gameMetadataDao.getById(gameId);
    if (gameMetadata == null) {
      gameMetadata = new GameMetadata(gameId);
    }
    gameMetadata.view();
    gameMetadataDao.save(gameMetadata);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String gameId = req.getParameter("gameId");
    Preconditions.checkNotNull(gameId, "GameId must be set.");
    GameDao gameDao = new GameDao();
    Game game = gameDao.getById(gameId);
    recordView(gameId);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson("game", game));
  }
}
