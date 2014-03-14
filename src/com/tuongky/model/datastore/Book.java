package com.tuongky.model.datastore;

import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;

public class Book {

  private @Id Long id;
  private String username;
  private String fbId;
  private long createdAt;
  @Unindexed private String name;

  @SuppressWarnings("unused")
  private Book() {} // Used by Objectify.

  public Book(String username, String fbId, String name) {
    this.username = username;
    this.createdAt = new Date().getTime();
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getFbId() {
    return fbId;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public String getName() {
    return name;
  }
}
