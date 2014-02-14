package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.UserMetadata;

import java.util.List;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadataDao extends DAOBase{

  static {
    ObjectifyRegister.register();
  }

  // TODO check if user exists before doing increment
  public static UserMetadataDao instance = new UserMetadataDao();

  //transactional
  public void solve(long userId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata == null) {
      userMetadata = new UserMetadata(userId);
    }

    userMetadata.incrementSolve();
    ofy.put(userMetadata);

    ofy.getTxn().commit();
  }

  //transactional
  public void attempt(long userId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata == null) {
      userMetadata = new UserMetadata(userId);
    }

    userMetadata.incrementAttempt();

    ofy.put(userMetadata);
    ofy.getTxn().commit();
  }

  public UserMetadata get(long userId) {
    return ObjectifyService.begin().find(UserMetadata.class, userId);

  }

  public List<UserMetadata> search(int offset, int limit){
    return ObjectifyService.begin().query(UserMetadata.class).offset(offset).limit(limit).order(UserMetadata.SOLVES_FIELD).list();
  }
}
