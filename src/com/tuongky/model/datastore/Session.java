package com.tuongky.model.datastore;

import java.util.UUID;

import javax.persistence.Id;

import com.tuongky.model.UserRole;

public class Session {

  private @Id String id;
  private long userId;
  private int roleIndex;

  @SuppressWarnings("unused") // Used by Objectify.
  private Session() {}

  public Session(long userId, UserRole userRole) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
    this.roleIndex = userRole.getValue();
  }

  public String getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public UserRole getUserRole() {
    return UserRole.fromValue(roleIndex);
  }
}
