package com.tuongky.servlet.user;

/**
 * Created by sngo on 2/16/14.
 */
public class RankInfo {
  private int rank;
  private int ties;
  private int totalRankedUsers;

  public RankInfo(int rank, int ties, int totalRankedUsers) {
    this.rank = rank;
    this.ties = ties;
    this.totalRankedUsers = totalRankedUsers;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public int getTies() {
    return ties;
  }

  public void setTies(int ties) {
    this.ties = ties;
  }

  public int getTotalRankedUsers() {
    return totalRankedUsers;
  }

  public void setTotalRankedUsers(int totalRankedUsers) {
    this.totalRankedUsers = totalRankedUsers;
  }
}
