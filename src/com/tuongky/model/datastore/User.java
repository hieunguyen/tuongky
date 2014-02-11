package com.tuongky.model.datastore;

import javax.persistence.Id;

import org.mindrot.BCrypt;

import com.googlecode.objectify.annotation.Unindexed;
import com.tuongky.Constants;
import com.tuongky.model.UserRole;

public class User {

  private @Id Long id;
  private int problemsSolved;
  private String email;
  private String username;
  @Unindexed private String hashed;
  private int roleIndex;

  private String fbId;
  @Unindexed private String fbName;

  private User() {}

  public User(String email, String username, String hashed) {
    this.email = email;
    this.username = username;
    this.hashed = hashed;
  }

  public static User createFbUser(String fbId, String fbName) {
    User user = new User();
    user.fbId = fbId;
    user.fbName = fbName;
    if (Constants.ADMIN_FB_IDS.contains(fbId)) {
      user.roleIndex = UserRole.ADMIN.getValue();
    } else {
      user.roleIndex = UserRole.USER.getValue();
    }
    return user;
  }

  public boolean isValidPassword(String pwd) {
    return BCrypt.checkpw(pwd, hashed);
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getUsername() {
    return username;
  }

  public String getHashed() {
    return hashed;
  }

  public String getFbId() {
    return fbId;
  }

  public void setFbName(String fbName) {
    this.fbName = fbName;
  }

  public String getFbName() {
    return fbName;
  }

  public int getRoleIndex() {
    return roleIndex;
  }

  public UserRole getUserRole() {
    return UserRole.fromValue(roleIndex);
  }
}
