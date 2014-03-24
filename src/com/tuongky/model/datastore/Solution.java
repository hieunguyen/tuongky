package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * Created by sngo on 2/9/14.
 */
public class Solution {

  // Id is always a concatenation of actor and problem
  // actor and problem uniquely identify a solution

  private static final String UNIQUE_DELIMINATOR = "#";

  private @Id String id;

  // refer to User.id
  private long actorId;

  // This field is the same as User.fbName. Easier to retrieve.
  private String actorName;

  // This field is the same as Problem.title. Easier to retrieve.
  private String problemTitle;

  // refer to Problem.id
  private long problemId;

  private long createdDate;

  @Parent private Key<User> userKey;

  @SuppressWarnings("unused") // Used by Objectify.
  private Solution() {}

  public Solution(User user, long problemId, String actorName, String problemTitle) {
    id = createId(user.getId(), problemId);
    this.actorId = user.getId();
    this.problemId = problemId;
    this.actorName = actorName;
    this.problemTitle = problemTitle;
    createdDate = System.currentTimeMillis();
    userKey = user.createKey();
  }

  public String getId() {
    return id;
  }

  public long getActorId() {
    return actorId;
  }

  public String getActorName() {
    return actorName;
  }

  public String getProblemTitle() {
    return problemTitle;
  }

  public long getProblemId() {
    return problemId;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public Key<User> getUserKey() {
    return userKey;
  }

  public static String createId(long actorId, long problemId) {
    return Long.toString(actorId) + UNIQUE_DELIMINATOR + Long.toString(problemId);
  }

  public static String ID_FIELD = "id";
  public static String ACTOR_ID_FIELD = "actorId";
  public static String PROBLEM_ID_FIELD = "problemId";
  public static String CREATED_DATE = "createdDate";
  public static String ACTOR_NAME_FIELD = "actorName";
  public static String PROBLEM_TITLE_FIELD = "problemTitle";
}
