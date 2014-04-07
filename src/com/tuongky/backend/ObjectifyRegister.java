package com.tuongky.backend;

import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.Book;
import com.tuongky.model.datastore.EmailHistory;
import com.tuongky.model.datastore.ExternalUser;
import com.tuongky.model.datastore.Game;
import com.tuongky.model.datastore.GameMetadata;
import com.tuongky.model.datastore.Invite;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.ProblemMetadata;
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.Ranker;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.SimpleCounter;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.Statistics;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;

/**
 * Created by sngo on 2/12/14.
 */
public class ObjectifyRegister {

  static {
    ObjectifyService.register(Book.class);
    ObjectifyService.register(EmailHistory.class);
    ObjectifyService.register(ExternalUser.class);
    ObjectifyService.register(Game.class);
    ObjectifyService.register(GameMetadata.class);
    ObjectifyService.register(Invite.class);
    ObjectifyService.register(Problem.class);
    ObjectifyService.register(ProblemAttempt.class);
    ObjectifyService.register(ProblemMetadata.class);
    ObjectifyService.register(ProblemUserMetadata.class);
    ObjectifyService.register(Ranker.class);
    ObjectifyService.register(Session.class);
    ObjectifyService.register(SimpleCounter.class);
    ObjectifyService.register(Solution.class);
    ObjectifyService.register(Statistics.class);
    ObjectifyService.register(User.class);
    ObjectifyService.register(UserMetadata.class);
  }

  private static final ObjectifyRegister instance = new ObjectifyRegister();

  public static ObjectifyRegister register() {
    return instance;
  }
}
