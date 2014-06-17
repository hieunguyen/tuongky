package com.tuongky.backend;

import java.util.List;
import java.util.Map;

import org.mindrot.BCrypt;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.User;

public class UserDao extends DAOBase {

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
    UserMetadataDao.instance.create(user);
    return user;
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

  public Map<Long, User> batchGetById(Iterable<Long> ids) {
    return ObjectifyService.begin().get(User.class, ids);
  }

  public List<User> getAll() {
    return ObjectifyService.begin().query(User.class).list();
  }
}
