package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;
import com.tuongky.model.GameCategory;

public class Game {

  private @Id String id;
  @Unindexed private int categoryIndex;
  @Unindexed private String title;
  @Unindexed private String nTitle;
  @Unindexed private String book;
  @Unindexed private String nBook;
  @Unindexed private String data;
  @Unindexed private String username;
  @Unindexed private String fbId;
  @Unindexed private String fbName;

  @SuppressWarnings("unused")
  private Game() {} // Used by Objectify.

  public Game(String id, String username, String fbId, String fbName,
      GameCategory category, String title, String nTitle, String book, String nBook, String data) {
    this.id = id;
    this.categoryIndex = category.getValue();
    this.title = title;
    this.nTitle = nTitle;
    this.book = book;
    this.nBook = nBook;
    this.data = data;
    this.username = username;
    this.fbId = fbId;
    this.fbName = fbName;
  }

  public Game(String username, String fbId, String fbName, GameCategory category,
      String title, String nTitle, String book, String nBook, String data) {
    this(null, username, fbId, fbName, category, title, nTitle, book, nBook, data);
  }

  public Game(String username, String fbId, String fbName, GameCategory category,
      String title, String nTitle, String book, String nBook) {
    this(null, username, fbId, fbName, category, title, nTitle, book, nBook, null);
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

  public String getNTitle() {
    return nTitle;
  }

  public String getBook() {
    return book;
  }

  public String getNBook() {
    return nBook;
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

  public String getFbId() {
    return fbId;
  }

  public String getFbName() {
    return fbName;
  }
}
