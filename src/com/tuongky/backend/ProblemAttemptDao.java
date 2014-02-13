package com.tuongky.backend;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.util.ProblemUtils;

/**
 * Created by sngo on 2/3/14.
 */
public class ProblemAttemptDao extends DAOBase{

  static {
    ObjectifyRegister.register();
  }

  public static ProblemAttemptDao instance = new ProblemAttemptDao();

  private static final Logger log = Logger.getLogger(ProblemAttemptDao.class.getName());

  /**
   * Save the outcome of an attempt made by a user, either success or fail
   *
   * @param actorId
   * @param problemId
   * @param isSuccess
   * @return id
   */
  public long attempt(long actorId, long problemId, boolean isSuccess) {

    ProblemAttempt attempt = ProblemUtils.newProblemAttempt(actorId, problemId, isSuccess);

    Key<ProblemAttempt> key = ObjectifyService.begin().put(attempt);

    if (ProblemDao.instance.addAttempter(problemId) == -1) {
      log.severe("Fail to add attempter to problem " + problemId);
    }

    UserMetadataDao.instance.attempt(actorId);

    return key.getId();
  }

  private static int PAGE_SIZE_DEFAULT = 20;

  /**
   * Given actorId, return problems solved by this actor, sorted by createdDate.
   *
   * @param actorId
   * @param isSuccess
   * @param pageNum
   * @return
   */
  public List<ProblemAttempt> searchByActor(long actorId, Boolean isSuccess, Integer pageSize, int pageNum) {

    if (pageSize == null) {
      pageSize = PAGE_SIZE_DEFAULT;
    }
    int startIndex = pageNum * pageSize;
    int count = pageSize;

    Iterable<ProblemAttempt> problemAttempts;
    if (isSuccess == null) {
      problemAttempts = ObjectifyService.begin().query(ProblemAttempt.class).
              filter(ProblemAttempt.ACTOR_ID_FIELD, actorId).order(ProblemUtils.MINUS + ProblemAttempt.CREATED_DATE).offset(startIndex).limit(count);
    } else {
      problemAttempts = ObjectifyService.begin().query(ProblemAttempt.class).
              filter(ProblemAttempt.ACTOR_ID_FIELD, actorId).filter(ProblemAttempt.SUCCESS_FIELD, isSuccess).
              offset(startIndex).limit(count);
    }

    return Lists.newArrayList(problemAttempts);
  }

  /**
   * Given a problemId, return users who have tried to solve it, sorted by createDate
   *
   * @param problemId
   * @param isSuccess
   * @return
   */
  public List<ProblemAttempt> searchByProblem(long problemId, Boolean isSuccess, Integer pageSize, int pageNum) {

    if (pageSize == null) {
      pageSize = PAGE_SIZE_DEFAULT;
    }
    int startIndex = pageNum * pageSize;
    int count = pageSize;

    Iterable<ProblemAttempt> problemAttempts;
    if (isSuccess == null) {
      problemAttempts = ObjectifyService.begin().query(ProblemAttempt.class).
              filter(ProblemAttempt.PROBLEM_ID_FIELD, problemId).order(ProblemUtils.MINUS + ProblemAttempt.CREATED_DATE).offset(startIndex).limit(count);
    } else {
      problemAttempts = ObjectifyService.begin().query(ProblemAttempt.class).
              filter(ProblemAttempt.PROBLEM_ID_FIELD, problemId).filter(ProblemAttempt.SUCCESS_FIELD, isSuccess).
              offset(startIndex).limit(count);
    }

    return Lists.newArrayList(problemAttempts);
  }
}
