package com.tuongky.service;

/**
 * Created by sngo on 3/6/14.
 */
public enum MailTemplate {
  FIRST_TIMER("templates/first_timer_email.html", "Chào mừng bạn đến với tuongky.com"),
  LEVEL_DOWN("templates/level_down_email.html", "Có Thêm Bài Mới"),
  LEVEL_UP("templates/level_up_email.html", "Bạn Đã Được Tăng Level!");

  public String template;
  public String subject;

  MailTemplate(String template, String subject) {
    this.template = template;
    this.subject = subject;
  }
}
