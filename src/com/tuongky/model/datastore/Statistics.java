package com.tuongky.model.datastore;

import javax.persistence.Id;
import java.util.Date;

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

  private Statistics() {

  }

  public Statistics(long users, long views, long solves, long attempts) {
    this.users = users;
    this.views = views;
    this.solves = solves;
    this.attempts = attempts;

    date = new Date();
  }
}
