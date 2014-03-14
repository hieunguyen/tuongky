package com.tuongky.model.datastore;

import javax.annotation.Nullable;
import javax.persistence.Id;

import org.mindrot.BCrypt;

import com.googlecode.objectify.annotation.Unindexed;
import com.tuongky.model.UserRole;

public class User {

  private @Id Long id;
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

  public static User createFbUser(
      String fbId, String fbName, @Nullable String email, UserRole role) {
    User user = new User();
    user.fbId = fbId;
    user.fbName = fbName;
    user.email = email;
    user.setUserRole(role);
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

  public void setEmail(String email) {
    this.email = email;
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

  public void setUserRole(UserRole userRole) {
    this.roleIndex = userRole.getValue();
  }

  public UserRole getUserRole() {
    return UserRole.fromValue(roleIndex);
  }
}
