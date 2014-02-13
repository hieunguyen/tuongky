package com.tuongky.util;

<<<<<<< HEAD:src/com/tuongky/util/ProblemUtils.java
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Solution;

import java.util.logging.Logger;
=======
import java.util.logging.Logger;

import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Solution;
>>>>>>> 2ac6c54723e2e8f413b146c0412fe1e28dc022c8:src/com/tuongky/backend/Utils.java

/**
 * Created by sngo on 2/10/14.
 */
public class ProblemUtils {

  public static Logger log = Logger.getLogger(ProblemUtils.class.getName());

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
