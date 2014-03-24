package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

import java.util.UUID;

/**
 * Represent an attempt to solve one problem. It can be successful or unsuccessful.
 *
 * @author sngo
 */
public class ProblemAttempt {

  private @Id String id;

  // refer to User.id
  private long actorId;

  // This field is the same as User.fbName. Easier to retrieve.
  private String actorName;

  // This field is the same as Problem.title. Easier to retrieve.
  private String problemTitle;

  // refer to Problem.id
  private long problemId;

  private boolean isSuccessful;
  private long createdDate;

  @Parent private Key<User> userKey;

  protected ProblemAttempt() {
  }

  public ProblemAttempt(User creator, long problemId,
      String creatorName, String problemTitle, boolean isSuccessful) {
    this.id = UUID.randomUUID().toString();
    this.actorId = creator.getId();
    this.problemId = problemId;
    this.actorName = creatorName;
    this.problemTitle = problemTitle;
    this.isSuccessful = isSuccessful;
    createdDate = System.currentTimeMillis();
    userKey = creator.createKey();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public boolean isSuccessful() {
    return isSuccessful;
  }

  public void setSuccessful(boolean isSuccessful) {
    this.isSuccessful = isSuccessful;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public String getActorName() {
    return actorName;
  }

  public void setActorName(String actorName) {
    this.actorName = actorName;
  }

  public String getProblemTitle() {
    return problemTitle;
  }

  public void setProblemTitle(String problemTitle) {
    this.problemTitle = problemTitle;
  }

  public static String ID_FIELD = "id";
  public static String ACTOR_ID_FIELD = "actorId";
  public static String PROBLEM_ID_FIELD = "problemId";
  public static String SUCCESS_FIELD = "isSuccessful";
  public static String CREATED_DATE = "createdDate";
  public static String ACTOR_NAME_FIELD = "actorName";
  public static String PROBLEM_TITLE_FIELD = "problemTitle";
}
