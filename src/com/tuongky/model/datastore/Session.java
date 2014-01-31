package com.tuongky.model.datastore;

import java.util.UUID;

import javax.persistence.Id;

public class Session {

  private @Id String id;
  private long userId;
  private String username;

  @SuppressWarnings("unused") // Used by Objectify.
  private Session() {}

  public Session(long userId, String username) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
    this.username = username;
  }

  public String getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }
}
