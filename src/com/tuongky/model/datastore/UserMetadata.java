package com.tuongky.model.datastore;

import javax.persistence.Id;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadata {
  private @Id long id;
  private int solves = 0;
  private int attempts = 0;

  private UserMetadata(){
    // unused
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
}
