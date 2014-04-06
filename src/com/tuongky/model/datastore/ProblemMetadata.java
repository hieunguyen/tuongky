package com.tuongky.model.datastore;

import javax.persistence.Id;

public class ProblemMetadata {

  private @Id long id;
  private int views;

  @SuppressWarnings("unused") // Used by Objectify.
  private ProblemMetadata() {}

  public ProblemMetadata(long id) {
    this.id = id;
    this.views = 0;
  }

  public void view() {
    views++;
  }

  public long getId() {
    return id;
  }

  public int getViews() {
    return views;
  }
}
