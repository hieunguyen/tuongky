package com.tuongky.service.email;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.User;

import java.util.logging.Logger;

/**
 * Created by sngo on 3/9/14.
 */
public class EmailTaskQueueService {

  private static final Logger log = Logger.getLogger(EmailTaskQueueService.class.getName());
  public static final EmailTaskQueueService instance = new EmailTaskQueueService();
  private static final String WELCOME_EMAIL_SERVLET = "/mail/welcome";
  private static final String LEVEL_UP_EMAIL_SERVLET = "/mail/levelup";
  private static final String EMAIL_QUEUE = "email-queue";

  public void pushWelcomeEmail(long userId) {
    User user = UserDao.instance.getById(userId);
    String email = user.getEmail();

    if (email != null && !email.isEmpty()) {
      Queue queue = QueueFactory.getQueue(EMAIL_QUEUE);
      queue.add(TaskOptions.Builder.withUrl(WELCOME_EMAIL_SERVLET + "?address=" + email + "&name=" + user.getFbName()));
    }
  }

  public void pushLevelUpEmail(long userId, int oldLevel, int newLevel) {
    User user = UserDao.instance.getById(userId);
    String email = user.getEmail();

    if (email != null && !email.isEmpty()) {
      Queue queue = QueueFactory.getQueue(EMAIL_QUEUE);
      queue.add(TaskOptions.Builder.withUrl(LEVEL_UP_EMAIL_SERVLET + "?address=" + email + "&name=" + user.getFbName() +
              "&old_level=" + oldLevel + "&new_level" + newLevel));
    }

  }
}
