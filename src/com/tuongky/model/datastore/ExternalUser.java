package com.tuongky.model.datastore;

import javax.persistence.Id;

public class ExternalUser {

  private static final String FB_ID_PREFIX = "fb_";

  private @Id String id;

  @SuppressWarnings("unused") // Used by Objectify.
  private ExternalUser() {}

  public ExternalUser(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public static String createIdFromFbId(String fbId) {
    return FB_ID_PREFIX + fbId;
  }
}
