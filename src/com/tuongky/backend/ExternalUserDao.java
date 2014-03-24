package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.ExternalUser;

public class ExternalUserDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  public static final ExternalUserDao instance = new ExternalUserDao();

  public ExternalUser getById(String id) {
    return getById(id, ObjectifyService.begin());
  }

  public ExternalUser getById(String id, Objectify ofy) {
    return ofy.find(ExternalUser.class, id);
  }

  public ExternalUser save(String id) {
    return save(id, ObjectifyService.begin());
  }

  public ExternalUser save(String id, Objectify ofy) {
    ExternalUser externalUser = new ExternalUser(id);
    ofy.put(externalUser);
    return externalUser;
  }
}
