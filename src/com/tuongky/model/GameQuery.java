package com.tuongky.model;

public class GameQuery {

  private final String queryString;
  private final String category;
  private final String title;
  private final String book;
  private final int offset;

  public GameQuery(String queryString, String category, String title, String book, int offset) {
    this.queryString = queryString;
    this.category = category;
    this.title = title;
    this.book = book;
    this.offset = offset;
  }

  public String getQueryString() {
    return queryString;
  }

  public String getCategory() {
    return category;
  }

  public String getTitle() {
    return title;
  }

  public String getBook() {
    return book;
  }

  public int getOffset() {
    return offset;
  }

  private boolean hasContent(String s) {
    return s != null && s.trim().length() > 0;
  }

  public boolean hasQueryString() {
    return hasContent(queryString);
  }

  public boolean hasCategory() {
    return hasContent(category);
  }

  public boolean hasTitle() {
    return hasContent(title);
  }

  public boolean hasBook() {
    return hasContent(book);
  }
}
