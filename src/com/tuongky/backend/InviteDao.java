package com.tuongky.backend;

import java.util.UUID;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Invite;

public class InviteDao extends DAOBase {

  static {
    ObjectifyService.register(Invite.class);
  }

  public Invite save() {
    Invite invite = new Invite(UUID.randomUUID().toString());
    ObjectifyService.begin().put(invite);
    return invite;
  }

  public Invite save(Invite invite) {
    ObjectifyService.begin().put(invite);
    return invite;
  }

  public Invite getById(String id) {
    return getById(id, ObjectifyService.begin());
  }

  public Invite getById(String id, Objectify ofy) {
    return ofy.get(Invite.class, id);
  }
}
