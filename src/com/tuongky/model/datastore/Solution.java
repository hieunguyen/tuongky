package com.tuongky.model.datastore;

/**
 * Created by sngo on 2/9/14.
 */
public class Solution extends ProblemAttempt {
  // Id is always a concatenation of actor and problem
  // actor and problem uniquely identify a solution

  private static String UNIQUE_DELIMINATOR = "#";

  private Solution(){
    super();
  }
  public Solution(long actorId, long problemId, String actorName, String problemTitle) {
    super(actorId, problemId, actorName, problemTitle, true);
    setId(getId(actorId, problemId));
  }

  public String getId (long actorId, long problemId) {
    return Long.toString(actorId) + UNIQUE_DELIMINATOR + Long.toString(problemId);
  }

}
