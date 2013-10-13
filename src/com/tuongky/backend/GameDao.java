package com.tuongky.backend;

import java.util.UUID;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.GameCategory;
import com.tuongky.model.datastore.Game;

public class GameDao extends DAOBase {

  static {
    ObjectifyService.register(Game.class);
  }

  public Game save(String username,
      GameCategory category, String title, String book, String data) {
    String id = UUID.randomUUID().toString();
    Game gameData = new Game(id, username, category, title, book, data);
    ObjectifyService.begin().put(gameData);
    return gameData;
  }

  public Game save(String id, String username,
      GameCategory category, String title, String book, String data) {
    Game gameData = new Game(id, username, category, title, book, data);
    ObjectifyService.begin().put(gameData);
    return gameData;
  }

  public Game add(Game game) {
    return save(
        game.getUsername(), game.getCategory(), game.getTitle(), game.getBook(), game.getData());
  }

  public Game save(Game game) {
    ObjectifyService.begin().put(game);
    return game;
  }

  public Game getById(String id) {
    return ObjectifyService.begin().get(Game.class, id);
  }
}
