package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadata {

  // Same as User.id
  private @Id Long id;
  private int solves = 0;
  private int attempts = 0;

  @Parent private Key<User> userKey;

  public static final String SOLVES_FIELD = "solves";
  public static final String ATTEMPTS_FIELD = "attempts";

  @SuppressWarnings("unused") // Used by Objectify.
  private UserMetadata() {
  }

  public UserMetadata(Key<User> userKey) {
    this.id = userKey.getId();
    this.userKey = userKey;
  }

  public Long getId() {
    return id;
  }

  public int getSolves() {
    return solves;
  }

  public void setSolves(int solves) {
    this.solves = solves;
  }

  public int getAttempts() {
    return attempts;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public void incrementSolve() {
    solves++;
  }

  public void incrementAttempt() {
    attempts++;
  }

  public Key<User> getUserKey() {
    return userKey;
  }
}
