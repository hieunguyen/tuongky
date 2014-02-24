package com.tuongky.backend;

import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.*;

/**
 * Created by sngo on 2/12/14.
 */
public class ObjectifyRegister {

  private static final ObjectifyRegister instance = new ObjectifyRegister();

  static {
    ObjectifyService.register(Book.class);
    ObjectifyService.register(Game.class);
    ObjectifyService.register(GameMetadata.class);
    ObjectifyService.register(Invite.class);
    ObjectifyService.register(ProblemAttempt.class);
    ObjectifyService.register(Problem.class);
    ObjectifyService.register(Session.class);
    ObjectifyService.register(Solution.class);
    ObjectifyService.register(User.class);
    ObjectifyService.register(UserMetadata.class);
    ObjectifyService.register(SimpleCounter.class);
    ObjectifyService.register(ProblemUserMetadata.class);
  }

  public static ObjectifyRegister register(){
    return instance;
  }
}
