package com.tuongky.backend;

import java.util.Map;

import javax.annotation.Nullable;

import org.mindrot.BCrypt;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.User;
import com.tuongky.service.email.EmailTaskQueueService;

public class UserDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  public static final UserDao instance = new UserDao();

  public User save(User user) {
    ObjectifyService.begin().put(user);
    return user;
  }

  public User save(String email, String username, String password) {
    String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
    User user = new User(email, username, hashed);
    ObjectifyService.begin().put(user);

    // create a new userMetadata
    UserMetadataDao.instance.create(user.getId());
    return user;
  }

  public User save(String fbId, String fbName, @Nullable String email, UserRole role) {
    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      User user = getByFbId(fbId);

      if (user == null) {
        user = User.createFbUser(fbId, fbName, email, role);
        ofy.put(user);
        // create a new userMetadata
        UserMetadataDao.instance.create(user.getId());
        // send welcome email
        EmailTaskQueueService.instance.pushWelcomeEmail(user.getId());
      } else {
        user.setFbName(fbName);
        if (email != null) {
          user.setEmail(email);
        }
        user.setUserRole(role);
        ofy.put(user);
      }

      ofy.getTxn().commit();
      return user;
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }

  public User getById(long id) {
    return getById(id, ObjectifyService.begin());
  }

  public User getById(long id, Objectify ofy) {
    return ofy.find(User.class, id);
  }

  public User getByEmail(String email) {
    return getByEmail(email, ObjectifyService.begin());
  }

  public User getByEmail(String email, Objectify ofy) {
    return ofy.query(User.class)
        .filter("email", email)
        .get();
  }

  public User getByFbId(String fbId) {
    return getByFbId(fbId, ObjectifyService.begin());
  }

  public User getByFbId(String fbId, Objectify ofy) {
    return ofy.query(User.class)
        .filter("fbId", fbId)
        .get();
  }

  public User getByUsername(String username) {
    return getByUsername(username, ObjectifyService.begin());
  }

  public User getByUsername(String username, Objectify ofy) {
    return ofy.query(User.class)
        .filter("username", username)
        .get();
  }

  public Map<Long, User> batchGetById(Iterable<Long> ids){
    return ObjectifyService.begin().get(User.class, ids);
  }

}
