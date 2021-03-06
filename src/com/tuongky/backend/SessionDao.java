package com.tuongky.backend;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.Session;

public class SessionDao {

  static {
    ObjectifyService.register(Session.class);
  }

  public Session save(long userId, String username) {
    Session session = new Session(userId, username);
    ObjectifyService.begin().put(session);
    return session;
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
