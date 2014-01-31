package com.tuongky.model;

public enum GameCategory {
  UNKNOWN(0),
  MATCH(1),
  OPENING(2),
  MIDDLE_GAME(3),
  END_GAME(4),
  POSTURE(5);

  private final int value;

  GameCategory(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean isUnKnown() {
    return this == UNKNOWN;
  }

  public static GameCategory fromValue(int value) {
    for (GameCategory gameCategory : GameCategory.values()) {
      if (gameCategory.getValue() == value) {
        return gameCategory;
      }
    }
    return GameCategory.UNKNOWN;
  }
}
