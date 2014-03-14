package com.tuongky.model.datastore;

import javax.annotation.Nullable;
import javax.persistence.Id;

/**
 * Record the history of emails sent to each user to avoid spamming.
 *
 * Created by sngo on 3/9/14.
 */
public class EmailHistory {

  private @Id long id;  // == userId

  @Nullable
  private Integer lastLevelNotified; // last level change notified
  @Nullable
  private Double lastTimeReminded; // last time we sent reminder email to user
  @Nullable
  private Double currentReminderPeriod; // increase exponentially

  private EmailHistory() {
    // unused
  }
  public EmailHistory(long id, int lastLevelNotified) {
    this.id = id;
    this.lastLevelNotified = lastLevelNotified;
  }

  public EmailHistory(long id, Double lastTimeReminded, double currentReminderPeriod) {
    this.lastTimeReminded = lastTimeReminded;
    this.id = id;
    this.currentReminderPeriod = currentReminderPeriod;
  }

  public long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getLastLevelNotified() {
    return lastLevelNotified;
  }

  public void setLastLevelNotified(int lastLevelNotified) {
    this.lastLevelNotified = lastLevelNotified;
  }

  public Double getLastTimeReminded() {
    return lastTimeReminded;
  }

  public void setLastTimeReminded(Double lastTimeReminded) {
    this.lastTimeReminded = lastTimeReminded;
  }

  public Double getCurrentReminderPeriod() {
    return currentReminderPeriod;
  }

  public void setCurrentReminderPeriod(double currentReminderPeriod) {
    this.currentReminderPeriod = currentReminderPeriod;
  }
}
