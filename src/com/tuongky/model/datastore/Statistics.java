package com.tuongky.model.datastore;

import java.util.Date;

import javax.persistence.Id;

/**
 * Created by sngo on 4/6/14.
 */
public class Statistics {
  private @Id Long id;
  private Date date;

  private long users;

  private long views;

  private long solves;

  private long attempts;

  @SuppressWarnings("unused") // Used by Objectify.
  private Statistics() {
  }

  public Statistics(long users, long views, long solves, long attempts) {
    this.users = users;
    this.views = views;
    this.solves = solves;
    this.attempts = attempts;

    date = new Date();
  }

  public Long getId() {
    return id;
  }

  public Date getDate() {
    return date;
  }

  public long getUsers() {
    return users;
  }

  public long getViews() {
    return views;
  }

  public long getSolves() {
    return solves;
  }

  public long getAttempts() {
    return attempts;
  }
}
