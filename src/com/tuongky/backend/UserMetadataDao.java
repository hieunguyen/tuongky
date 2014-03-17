package com.tuongky.backend;

import java.util.List;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.service.email.EmailTaskQueueService;
import com.tuongky.util.ProblemUtils;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadataDao extends DAOBase{

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

  public static int computeLevel(int solves) {
    return (solves * 12) / (int)CounterDao.getProblemsCount();
  }

  public void setSolves(long userId, int solves) {
    Objectify ofy = ObjectifyService.beginTransaction();

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata != null) {
      userMetadata.setSolves(solves);
    }

    ofy.put(userMetadata);

    ofy.getTxn().commit();
  }

  //transactional
  public void solve(long userId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    UserMetadata userMetadata = ofy.find(UserMetadata.class, userId);

    if (userMetadata == null) {
      userMetadata = create(userId);
    }

    int oldLevel = computeLevel(userMetadata.getSolves());
    userMetadata.incrementSolve();

    // update the ranker
    UserRankerDao.instance.increaseRank(userMetadata.getSolves());

    ofy.put(userMetadata);

    ofy.getTxn().commit();

    int newLevel = computeLevel(userMetadata.getSolves());
    if (oldLevel != newLevel) {
      EmailTaskQueueService.instance.pushLevelUpEmail(userId, oldLevel, newLevel);
    }
    EmailHistoryDao.instance.save(newLevel);
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

  public List<UserMetadata> getAllUsers() {
    return ObjectifyService.begin().query(UserMetadata.class).list();
  }
}
