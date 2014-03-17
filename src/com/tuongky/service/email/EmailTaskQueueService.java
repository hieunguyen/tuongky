package com.tuongky.service.email;

import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.User;
import com.tuongky.servlet.mail.LevelDownEmailServlet;
import com.tuongky.servlet.mail.LevelUpEmailServlet;
import com.tuongky.servlet.mail.SpamReminderEmailServlet;
import com.tuongky.servlet.mail.WelcomeEmailServlet;

/**
 * Created by sngo on 3/9/14.
 */
public class EmailTaskQueueService {

  private static final Logger log = Logger.getLogger(EmailTaskQueueService.class.getName());
  public static final EmailTaskQueueService instance = new EmailTaskQueueService();

  private static final String WELCOME_EMAIL_SERVLET = "/mail/welcome";
  private static final String LEVEL_UP_EMAIL_SERVLET = "/mail/levelup";
  private static final String LEVEL_DOWN_EMAIL_SERVLET = "/mail/leveldown";
  private static final String SPAM_REMINDER_EMAIL_SERVLET = "/mail/inactive_reminder";
  private static final String EMAIL_QUEUE = "email-queue";

  public void pushWelcomeEmail(long userId) {
    User user = UserDao.instance.getById(userId);

    if (user == null) {
      log.severe("User not found: " + userId);
      return;
    }

    String email = user.getEmail();

    if (email != null && !email.isEmpty()) {
      Queue queue = QueueFactory.getQueue(EMAIL_QUEUE);
      queue.add(TaskOptions.Builder.withUrl(WELCOME_EMAIL_SERVLET).param(WelcomeEmailServlet.ADDRESS_FIELD, email)
              .param(WelcomeEmailServlet.USER_NAME_FIELD, user.getFbName()));
    }
  }

  public void pushSpamReminderEmail(long userId) {
    User user = UserDao.instance.getById(userId);

    if (user == null) {
      log.severe("User not found: " + userId);
      return;
    }

    String email = user.getEmail();

    if (email != null && !email.isEmpty()) {
      Queue queue = QueueFactory.getQueue(EMAIL_QUEUE);
      queue.add(TaskOptions.Builder.withUrl(SPAM_REMINDER_EMAIL_SERVLET).param(SpamReminderEmailServlet.ADDRESS_FIELD, email)
              .param(SpamReminderEmailServlet.USER_NAME_FIELD, user.getFbName()));
    }
  }

  public void pushLevelUpEmail(long userId, int oldLevel, int newLevel) {
    User user = UserDao.instance.getById(userId);

    if (user == null) {
      log.severe("User not found: " + userId);
      return;
    }

    String email = user.getEmail();

    if (email != null && !email.isEmpty()) {
      Queue queue = QueueFactory.getQueue(EMAIL_QUEUE);
      queue.add(TaskOptions.Builder.withUrl(LEVEL_UP_EMAIL_SERVLET).param(LevelUpEmailServlet.ADDRESS_FIELD, email)
              .param(LevelUpEmailServlet.USER_NAME_FIELD, user.getFbName())
              .param(LevelUpEmailServlet.OLD_LEVEL_FIELD, String.valueOf(oldLevel))
              .param(LevelUpEmailServlet.NEW_LEVEL_FIELD, String.valueOf(newLevel)));
    }

  }

  public void pushLevelDownEmail(long userId, int oldLevel, int newLevel) {
    User user = UserDao.instance.getById(userId);

    if (user == null) {
      log.severe("User not found: " + userId);
      return;
    }
    String email = user.getEmail();

    if (email != null && !email.isEmpty()) {
      Queue queue = QueueFactory.getQueue(EMAIL_QUEUE);
      queue.add(TaskOptions.Builder.withUrl(LEVEL_DOWN_EMAIL_SERVLET).param(LevelDownEmailServlet.ADDRESS_FIELD, email)
              .param(LevelDownEmailServlet.USER_NAME_FIELD, user.getFbName())
              .param(LevelDownEmailServlet.OLD_LEVEL_FIELD, String.valueOf(oldLevel))
              .param(LevelDownEmailServlet.NEW_LEVEL_FIELD, String.valueOf(newLevel)));
    }

  }

}
