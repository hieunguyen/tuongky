package com.tuongky.backend;

import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by sngo on 2/10/14.
 */
public class Utils {

  public static Logger log = Logger.getLogger(Utils.class.getName());

  public static String MINUS = "-";

  public static ProblemAttempt newProblemAttempt(long actorId, long problemId, Boolean isSuccessful) {
    String actorName = "";

    try {
      actorName = UserDao.instance.getById(actorId).getUsername();
    } catch (Exception e) {
      log.severe("User not found: " + actorId);
    }

    String problemTitle = "";

    try {
      problemTitle = ProblemDao.instance.getById(problemId).getTitle();
    } catch (Exception e) {
      log.severe("Problem not found: " + problemId);
    }

    if (isSuccessful == null) {
      return new Solution(actorId, problemId, actorName, problemTitle);
    } else {
      return new ProblemAttempt(actorId, problemId, actorName, problemTitle, isSuccessful);
    }
  }
}
