package com.tuongky.model;

public enum UserRole {
  ANONYMOUS(0),
  USER(1),
  MODERATOR(2),
  ADMIN(3);

  private final int value;

  UserRole(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static UserRole fromValue(int value) {
    for (UserRole role : UserRole.values()) {
      if (role.getValue() == value) {
        return role;
      }
    }
    return ANONYMOUS;
  }
}
