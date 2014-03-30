package com.tuongky.service;

/**
 * Created by sngo on 3/6/14.
 */
public enum MailTemplate {
  FIRST_TIMER("templates/first_timer_email.html", "Chào mừng bạn đến với tuongky.com"),
  LEVEL_DOWN("templates/level_down_email.html", "Có thêm bài mới."),
  LEVEL_UP("templates/level_up_email.html", "Chúc mừng bạn đã lên trình!"),
  SPAM_REMINDER("templates/spam_reminder.html", "Có thêm bài mới.");

  private String template;
  private String subject;

  private MailTemplate(String template, String subject) {
    this.template = template;
    this.subject = subject;
  }

  public String getTemplate() {
    return template;
  }

  public String getSubject() {
    return subject;
  }
}
