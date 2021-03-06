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
      GameCategory category, String title, String nTitle, String book, String nBook, String data) {
    String id = UUID.randomUUID().toString();
    Game game = new Game(id, username, category, title, nTitle, book, nBook, data);
    ObjectifyService.begin().put(game);
    return game;
  }

  public Game save(String id, String username,
      GameCategory category, String title, String nTitle, String book, String nBook, String data) {
    Game game = new Game(id, username, category, title, nTitle, book, nBook, data);
    ObjectifyService.begin().put(game);
    return game;
  }

  public Game add(Game game) {
    return save(
        game.getUsername(), game.getCategory(),
        game.getTitle(), game.getNTitle(), game.getBook(), game.getNBook(), game.getData());
  }

  public Game save(Game game) {
    ObjectifyService.begin().put(game);
    return game;
  }

  public Game getById(String id) {
    return ObjectifyService.begin().find(Game.class, id);
  }

  public void delete(String id) {
    ObjectifyService.begin().delete(Game.class, id);
  }
}
