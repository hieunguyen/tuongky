package com.tuongky.backend;

import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.User;
import com.tuongky.util.ProblemUtils;

/**
 * Created by sngo on 2/3/14.
 */
public class ProblemAttemptDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  public static ProblemAttemptDao instance = new ProblemAttemptDao();

  public ProblemAttempt getById(long userId, String id) {
    Key<User> parentKey = Key.create(User.class, userId);
    Key<ProblemAttempt> key = Key.create(parentKey, ProblemAttempt.class, id);
    return ObjectifyService.begin().find(key);
  }

  public void setAttemptStatus(String id, boolean isSuccess) {
    ProblemAttempt attempt = ObjectifyService.begin().find(ProblemAttempt.class, id);
    if (attempt != null){
      attempt.setSuccessful(isSuccess);
      ObjectifyService.begin().put(attempt);
    }
  }

  private static int PAGE_SIZE_DEFAULT = 20;

  /**
   * Given actorId, return problems attempted by this actor, sorted by createdDate.
   *
   * @param actorId
   * @param isSuccess
   * @param pageNum
   * @return
   */
  public List<ProblemAttempt> searchByActor(
      long actorId, Boolean isSuccess, Integer pageSize, int pageNum) {

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

  public List<ProblemAttempt> find(int offSet, int limit) {
    return ObjectifyService.begin().query(ProblemAttempt.class).order(ProblemUtils.MINUS + ProblemAttempt.CREATED_DATE)
            .offset(offSet).limit(limit).list();
  }

  public List<ProblemAttempt> findLastFailedAttempts(long actorId) {
    List<ProblemAttempt> attempts = searchByActor(actorId, null, 100, 0);
    List<ProblemAttempt> failedAttempts = Lists.newArrayList();
    for (int i = 0; i < attempts.size(); i++)
    if (!attempts.get(i).isSuccessful()) {
      boolean interesting = true;
      for (int j = 0; j < i; j++) {
        if (attempts.get(i).getProblemId() == attempts.get(j).getProblemId()) {
          interesting = false;
          break;
        }
      }
      if (interesting) {
        failedAttempts.add(attempts.get(i));
      }
    }
    return failedAttempts;
  }
}
