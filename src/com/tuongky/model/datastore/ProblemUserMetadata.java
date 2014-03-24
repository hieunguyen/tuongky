package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * This class stores information between a user and a problem
 *
 * Created by sngo on 2/23/14.
 */
public class ProblemUserMetadata {

  private @Id String id;

  private long actorId;
  private long problemId;

  private int attempts = 0;

  @Parent private Key<User> userKey;

  private static String UNIQUE_DELIMINATOR = "#";

  @SuppressWarnings("unused") // Used by Objectify.
  private ProblemUserMetadata() {}

  public static String createId (long actorId, long problemId) {
    return Long.toString(actorId) + UNIQUE_DELIMINATOR + Long.toString(problemId);
  }

  public ProblemUserMetadata(User user, long problemId) {
    this.actorId = user.getId();
    this.problemId = problemId;
    id = Solution.createId(actorId, problemId);
    userKey = user.createKey();
  }

  public void incrementAttempt() {
    attempts++;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getAttempts() {
    return attempts;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public long getActorId() {
    return actorId;
  }

  public void setActorId(long actorId) {
    this.actorId = actorId;
  }

  public long getProblemId() {
    return problemId;
  }

  public void setProblemId(long problemId) {
    this.problemId = problemId;
  }
}
