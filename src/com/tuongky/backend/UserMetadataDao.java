package com.tuongky.backend;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.util.ProblemUtils;

/**
 * Created by sngo on 2/10/14.
 */
public class UserMetadataDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  public static final UserMetadataDao instance = new UserMetadataDao();

  // If userId already existed and then being overwritten, the count wil not be correct.
  // We do not check for existence before creating the new user, rather the client needs to be sure the userId doesn't exists
  // before calling this method
  public UserMetadata create(User user) {
    return create(user, ObjectifyService.begin());
  }

  public UserMetadata create(User user, Objectify ofy) {
    UserMetadata userMetadata = new UserMetadata(user.createKey());
    ofy.put(userMetadata);
    return userMetadata;
  }

  public static int computeLevel(int solves) {
    return (solves * 12) / (int) CounterDao.getProblemsCount();
  }

  public UserMetadata getByUser(User user, Objectify ofy) {
    Key<UserMetadata> key = Key.create(user.createKey(), UserMetadata.class, user.getId());
    return ofy.find(key);
  }

  public UserMetadata getByUser(User user) {
    return getByUser(user, ObjectifyService.begin());
  }

  // Rank by #problemSolves, if equals, rank by #problemAttempts
  public List<UserMetadata> search(int offset, int limit) {
    return ObjectifyService.begin()
        .query(UserMetadata.class)
        .order(ProblemUtils.MINUS + UserMetadata.SOLVES_FIELD)
        .order(UserMetadata.ATTEMPTS_FIELD)
        .offset(offset)
        .limit(limit)
        .list();
  }

  public List<UserMetadata> getAllUserMetadatas() {
    return ObjectifyService.begin()
        .query(UserMetadata.class)
        .list();
  }
}
