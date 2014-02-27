package com.tuongky.model.datastore;

import java.util.UUID;

import javax.persistence.Id;

public class Session {

  private @Id String id;
  private long userId;

  @SuppressWarnings("unused") // Used by Objectify.
  private Session() {}

  public Session(long userId) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
  }

  public String getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }
}
