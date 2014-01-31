package com.tuongky.model.datastore;

import javax.persistence.Id;

public class GameMetadata {

  private @Id String id;
  private int views;

  @SuppressWarnings("unused") // Used by Objectify.
  private GameMetadata() {}

  public GameMetadata(String id) {
    this.id = id;
    this.views = 0;
  }

  public void view() {
    views++;
  }

  public String getId() {
    return id;
  }

  public int getViews() {
    return views;
  }
}
