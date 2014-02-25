package com.tuongky.model;

import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;

public class UserData {

  private final User user;
  private final UserMetadata userMetadata;

  public UserData(User user, UserMetadata userMetadata) {
    this.user = user;
    this.userMetadata = userMetadata;
  }

  public User getUser() {
    return user;
  }

  public UserMetadata getUserMetadata() {
    return userMetadata;
  }
}
