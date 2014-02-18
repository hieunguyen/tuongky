package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.util.ProblemUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadataDao extends DAOBase{

  private static final Logger log = Logger.getLogger(UserMetadataDao.class.getName());

  static {
    ObjectifyRegister.register();
  }

  // TODO check if user exists before doing increment
  public static final UserMetadataDao instance = new UserMetadataDao();

  // If userId already existed and then being overwritten, the count wil not be correct.
  // We do not check for existence before creating the new user, rather the client needs to be sure the userId doesn't exists
  // before calling this method
  public UserMetadata create(long userId){
    UserMetadata user = new UserMetadata(userId);
    ObjectifyService.begin().put(user);
    CounterDao.addUser();
    return user;
  }

  //transactional
  public void solve(long userId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata == null) {
      userMetadata = create(userId);
    }

    userMetadata.incrementSolve();

    // update the ranker
    UserRankerDao.instance.increaseRank(userMetadata.getSolves());

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
    UserMetadata user = ObjectifyService.begin().find(UserMetadata.class, userId);
    return user;
  }

  // Rank by #problemSolves, if equals, rank by #problemAttempts
  public List<UserMetadata> search(int offset, int limit){
    return ObjectifyService.begin().query(UserMetadata.class).order(ProblemUtils.MINUS + UserMetadata.SOLVES_FIELD).
            order(UserMetadata.ATTEMPTS_FIELD).offset(offset).limit(limit).list();
  }
}
