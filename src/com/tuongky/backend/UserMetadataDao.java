package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.UserMetadata;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadataDao extends DAOBase{

  static {
    ObjectifyRegister.register();
  }

  public static UserMetadataDao instance = new UserMetadataDao();

  public void solve(long userId, Objectify ofy) {

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata != null) {
      userMetadata.incrementSolve();
    }

    ofy.put(userMetadata);
  }

  public void attempt(long userId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata != null) {
      userMetadata.incrementAttempt();
    }

    ofy.getTxn().commit();
  }

  public UserMetadata get(long userId) {
    return ObjectifyService.begin().find(UserMetadata.class, userId);

  }
}
