package com.tuongky.backend;

import org.joda.time.Period;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.EmailHistory;
import com.tuongky.service.email.EmailTaskQueueService;

/**
 * Created by sngo on 3/9/14.
 */
public class EmailHistoryDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  private static final double EXP_FACTOR = 2;
  private static final double INITIAL_REMINDER_PERIOD =
      Period.days(7).toStandardDuration().getMillis();

  public static EmailHistoryDao instance = new EmailHistoryDao();

  public void save(long userId, int levelNotified) {
    EmailHistory emailHistory = new EmailHistory(userId, levelNotified);
    ObjectifyService.begin().put(emailHistory);
  }

  public void save(long userId) {
    EmailHistory emailHistory = new EmailHistory(
        userId, (double) System.currentTimeMillis(), INITIAL_REMINDER_PERIOD);
    ObjectifyService.begin().put(emailHistory);
  }

  public EmailHistory get(long userId) {
    return ObjectifyService.begin().find(EmailHistory.class, userId);
  }

  public void notifyLevelDown(long userId, int newLevel) {
    EmailHistory emailHistory = get(userId);

    if (emailHistory == null) {
      emailHistory = new EmailHistory(userId, newLevel);
    } else if (emailHistory.getLastLevelNotified() == null) {
      emailHistory.setLastLevelNotified(0);
    }

    if (emailHistory.getLastLevelNotified() > newLevel) {
      EmailTaskQueueService.instance.pushLevelDownEmail(
          userId, emailHistory.getLastLevelNotified(), newLevel);
      emailHistory.setLastLevelNotified(newLevel);
      ObjectifyService.begin().put(emailHistory);
    }
  }

  public void spamRemind(long userId) {
    EmailHistory emailHistory = get(userId);
    if (emailHistory == null) {
      emailHistory = new EmailHistory(userId,
          (double) System.currentTimeMillis(), INITIAL_REMINDER_PERIOD);
    } else if (emailHistory.getLastTimeReminded() == null) {
      emailHistory.setLastTimeReminded((double)System.currentTimeMillis());
      emailHistory.setCurrentReminderPeriod(INITIAL_REMINDER_PERIOD);
    } else if (emailHistory.getLastTimeReminded() + emailHistory.getCurrentReminderPeriod() >
        System.currentTimeMillis()) {
      EmailTaskQueueService.instance.pushSpamReminderEmail(userId);
      emailHistory.setLastTimeReminded((double)System.currentTimeMillis());
      emailHistory.setCurrentReminderPeriod(emailHistory.getCurrentReminderPeriod() * EXP_FACTOR);
    }
    ObjectifyService.begin().put(emailHistory);
  }
}
