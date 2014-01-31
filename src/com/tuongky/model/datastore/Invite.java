package com.tuongky.model.datastore;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;

public class Invite {

  private @Id String id;
  @Unindexed private int used;

  @SuppressWarnings("unused") // Used by Objectify.
  private Invite() {}

  public Invite(String id) {
    this.id = id;
    this.used = 0;
  }

  public String getId() {
    return id;
  }

  public int getUsed() {
    return used;
  }

  public void useIt() {
    used++;
  }

  public boolean hasUsed() {
    return used > 0;
  }
}
