package com.tuongky.model.datastore;

import javax.jdo.annotations.Index;
import javax.persistence.Id;

/**
 * Represent a chess problem to be solved, together with requirement.
 *
 * @author sngo
 */
public class Problem {
  // sequentially increasing
  private @Id long id;
  // encode a state of a chess game
  private String fen;
  // refer to User.id of the creatorId
  @Index
  private Long creatorId;

  private String title;

  private String description;
  // requirement, eg: moves limitation. Json format.
  private String requirement;
  // number of users who solved this problem
  private int solvers;
  private int attempters;

  private long createdDate;

  public static String CREATOR_FIELD = "creatorId";
  public static String ID_FIELD = "id";
  public static String CREATED_DATE_FIELD = "createdDate";

  private Problem(){
    // unused
  }

  public Problem(long id, String title, String fen, String description, String requirement, Long creatorId) {
    this.id = id;
    this.title = title;
    this.fen = fen;
    this.description = description;
    this.requirement = requirement;
    this.creatorId = creatorId;
    solvers = 0;
    createdDate = System.currentTimeMillis();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFen() {
    return fen;
  }

  public void setFen(String fen) {
    this.fen = fen;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getRequirement() {
    return requirement;
  }

  public void setRequirement(String requirement) {
    this.requirement = requirement;
  }

  public int getSolvers() {
    return solvers;
  }

  public void setSolvers(int solvers) {
    this.solvers = solvers;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int addSolver() {
    return solvers++;
  }

  public int addAttempter() {
    return attempters++;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }
}
