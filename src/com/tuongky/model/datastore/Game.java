package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;
import com.tuongky.model.GameCategory;

public class Game {

  private @Id String id;
  @Unindexed private int categoryIndex;
  @Unindexed private String title;
  @Unindexed private String book;
  @Unindexed private String data;
  @Unindexed private String username;

  @SuppressWarnings("unused")
  private Game() {} // Used by Objectify.

  public Game(String id, String username,
      GameCategory category, String title, String book, String data) {
    this.id = id;
    this.categoryIndex = category.getValue();
    this.title = title;
    this.book = book;
    this.data = data;
    this.username = username;
  }

  public Game(String username, GameCategory category, String title, String book, String data) {
    this(null, username, category, title, book, data);
  }

  public Game(String username, GameCategory category, String title, String book) {
    this(null, username, category, title, book, null);
  }

  public String getId() {
    return id;
  }

  public int getCategoryIndex() {
    return categoryIndex;
  }

  public GameCategory getCategory() {
    return GameCategory.fromValue(categoryIndex);
  }

  public String getTitle() {
    return title;
  }

  public String getBook() {
    return book;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getUsername() {
    return username;
  }
}
