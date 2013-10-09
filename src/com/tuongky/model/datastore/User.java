package com.tuongky.model.datastore;

import javax.persistence.Id;

import org.mindrot.BCrypt;

import com.googlecode.objectify.annotation.Unindexed;

public class User {

  private @Id Long id;
  private String username;
  @Unindexed private String hashed;

  @SuppressWarnings("unused") // Used by Objectify.
  private User() {}

  public User(String username, String hashed) {
    this.username = username;
    this.hashed = hashed;
  }

  public boolean isValidPassword(String pwd) {
    return BCrypt.checkpw(pwd, hashed);
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getHashed() {
    return hashed;
  }
}
