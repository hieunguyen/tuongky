package com.tuongky.model.datastore;

import javax.persistence.Id;

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

  private static String UNIQUE_DELIMINATOR = "#";

  private ProblemUserMetadata(){

  }

  public static String createId (long actorId, long problemId) {
    return Long.toString(actorId) + UNIQUE_DELIMINATOR + Long.toString(problemId);
  }

  public ProblemUserMetadata(long actorId, long problemId){
    id = Solution.createId(actorId, problemId);
    this.actorId = actorId;
    this.problemId = problemId;
  }

  public int increaseAndGetAttempts(){
    attempts++;
    return attempts;
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
