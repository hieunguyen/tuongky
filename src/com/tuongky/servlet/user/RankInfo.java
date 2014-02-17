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
}
