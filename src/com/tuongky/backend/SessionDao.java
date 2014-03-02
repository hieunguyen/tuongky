package com.tuongky.backend;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.User;

public class SessionDao {

  static {
    ObjectifyRegister.register();
  }

  public Session save(long userId, UserRole userRole) {
    Session session = new Session(userId, userRole);
    ObjectifyService.begin().put(session);
    return session;
  }

  public Session save(User user) {
    return save(user.getId(), user.getUserRole());
  }

  public void remove(String id) {
    Key<Session> key = Key.create(Session.class, id);
    ObjectifyService.begin().delete(key);
  }

  public Session getById(String id) {
    Objectify ofy = ObjectifyService.begin();
    return ofy.find(Session.class, id);
  }
}
