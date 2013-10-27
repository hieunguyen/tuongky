package com.tuongky.model.datastore;

import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;

public class Book {

  private @Id Long id;
  private String username;
  private long createdAt;
  @Unindexed private String name;
  @Unindexed private String normalizedName;

  @SuppressWarnings("unused")
  private Book() {} // Used by Objectify.

  public Book(String username, String name, String normalizedName) {
    this.username = username;
    this.createdAt = new Date().getTime();
    this.name = name;
    this.normalizedName = normalizedName;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public String getName() {
    return name;
  }

  public String getNormalizedName() {
    return normalizedName;
  }
}
