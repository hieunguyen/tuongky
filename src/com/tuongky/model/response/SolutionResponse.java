package com.tuongky.model.response;

import javax.persistence.Id;

import com.tuongky.model.datastore.Solution;

public class SolutionResponse {

  private @Id
  final String id;

  // refer to User.id
  private final long actorId;

  // This field is the same as User.fbName. Easier to retrieve.
  private final String actorName;

  // This field is the same as Problem.title. Easier to retrieve.
  private final String problemTitle;

  // refer to Problem.id
  private final long problemId;

  private final long createdDate;

  private SolutionResponse(
      String id, long actorId, String actorName,
      String problemTitle, long problemId, long createdDate) {
    this.id = id;
    this.actorId = actorId;
    this.actorName = actorName;
    this.problemTitle = problemTitle;
    this.problemId = problemId;
    this.createdDate = createdDate;
  }

  public static SolutionResponse fromSolution(Solution solution) {
    return new SolutionResponse(
        solution.getId(),
        solution.getActorId(),
        solution.getActorName(),
        solution.getProblemTitle(),
        solution.getProblemId(),
        solution.getCreatedDate());
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
}
