package com.tuongky.backend;

import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.Book;
import com.tuongky.model.datastore.Game;
import com.tuongky.model.datastore.GameMetadata;
import com.tuongky.model.datastore.Invite;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.SimpleCounter;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;

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
  }

  public static ObjectifyRegister register(){
    return instance;
  }
}
