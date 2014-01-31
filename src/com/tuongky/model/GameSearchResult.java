package com.tuongky.model;

import java.util.List;

import com.tuongky.model.datastore.Game;

public class GameSearchResult {

  private final List<Game> games;
  private final long numberFound;

  public GameSearchResult(List<Game> games, long numberFound) {
    this.games = games;
    this.numberFound = numberFound;
  }

  public List<Game> getGames() {
    return games;
  }

  public long getNumberFound() {
    return numberFound;
  }
}
