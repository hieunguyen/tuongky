package com.tuongky.model.datastore;

import javax.persistence.Id;

/**
 * Created by sngo on 2/9/14.
 */
public class Solution extends ProblemAttempt {
  // Id is always a concatenation of actor and problem
  // actor and problem uniquely identify a solution
  private @Id String id;

  private static String UNIQUE_DELIMINATOR = "#";

  public Solution(long actorId, long problemId, String actorName, String problemTitle) {
    super(actorId, problemId, actorName, problemTitle, true);
    id = getId(actorId, problemId);
  }

  public String getId (long actorId, long problemId) {
    return Long.toString(actorId) + UNIQUE_DELIMINATOR + Long.toString(problemId);
  }

  public String getTrueId() {
    return id;
  }
}
