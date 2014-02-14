package com.tuongky.backend;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.*;
import com.tuongky.util.ProblemUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by sngo on 2/9/14.
 */
public class SolutionDao extends DAOBase{

  static {
    ObjectifyRegister.register();
  }

  public static final SolutionDao instance = new SolutionDao();
  private static final Logger log = Logger.getLogger(Solution.class.getName());

  private static int PAGE_SIZE_DEFAULT = 20;

  private boolean doesSolutionExist(Solution solution) {
    Query<Solution> result = ObjectifyService.begin().query(Solution.class).filter(Solution.ID_FIELD, solution.getId());
    return result != null && result.get() != null;
  }

  // TODO
  // There might be double count.
  // To be 100% precise, this method should be atomic. However the chance that the same user solves the same problem at
  // the same time is extremely rare. So we don't have to optimize for that edge case.
  public String solve(long actorId, long problemId) {

    Solution solution = (Solution) ProblemUtils.newProblemAttempt(actorId, problemId, null);

    if (!doesSolutionExist(solution)) {
      if (ProblemDao.instance.addSolver(problemId) == -1) {
        log.warning("Fail to add solver to problem " + problemId);
      }
      UserMetadataDao.instance.solve(actorId);
    }

    ObjectifyService.begin().put(solution);

    return solution.getId();
  }

  /**
   * Given actorId, return problems solved by this actor, sorted by createdDate.
   *
   * @param actorId
   * @param pageNum
   * @return
   */
  public List<Solution> searchByActor(long actorId, Integer pageSize, int pageNum) {

    if (pageSize == null) {
      pageSize = PAGE_SIZE_DEFAULT;
    }
    int startIndex = pageNum * pageSize;
    int count = pageSize;

    return ObjectifyService.begin().query(Solution.class).filter(Solution.ACTOR_ID_FIELD, actorId).
            order(ProblemUtils.MINUS + Solution.CREATED_DATE).offset(startIndex).limit(count).list();
  }

  /**
   * Given a problemId, return users who have solved it, sorted by createDate
   *
   * @param problemId
   * @return
   */

  public List<Solution> searchByProblem(long problemId, Integer pageSize, int pageNum) {

    if (pageSize == null) {
      pageSize = PAGE_SIZE_DEFAULT;
    }
    int startIndex = pageNum * pageSize;
    int count = pageSize;

    return ObjectifyService.begin().query(Solution.class).filter(Solution.PROBLEM_ID_FIELD, problemId).
            order(ProblemUtils.MINUS + Solution.CREATED_DATE).offset(startIndex).limit(count).list();
  }

}
