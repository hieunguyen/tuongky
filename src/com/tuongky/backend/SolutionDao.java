package com.tuongky.backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.Solution;
import com.tuongky.util.ProblemUtils;

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

  public boolean hasSolved(long actorId, long problemId) {
    String id = Solution.createId(actorId, problemId);
    Solution result = ObjectifyService.begin().find(Solution.class, id);
    return result != null;
  }

  // TODO
  // There might be double count.
  // To be 100% precise, this method should be atomic. However the chance that the same user solves the same problem at
  // the same time is extremely rare. So we don't have to optimize for that edge case.
  public String solve(long actorId, long problemId) {

    Solution solution = (Solution) ProblemUtils.newProblemAttempt(actorId, problemId, null);

    if (!hasSolved(actorId, problemId)) {
      if (ProblemDao.instance.addSolver(problemId) == -1) {
        log.warning("Fail to add solver to problem " + problemId);
      }
      UserMetadataDao.instance.solve(actorId);
    }

    ObjectifyService.begin().put(solution);


    return solution.getId();
  }

  public int getSolves(long actorId) {
    List<Solution> solutions = ObjectifyService.begin().query(Solution.class).filter(Solution.ACTOR_ID_FIELD, actorId).list();

    Set<Long> problemsolved = new HashSet<>();
    for (Solution solution : solutions) {
      problemsolved.add(solution.getProblemId());
    }

    return problemsolved.size();
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

  /**
   * Finds out which problems in the list have been solved by an user.
   * @param userId The userId.
   * @param problemIds A list of problemIds.
   * @return
   */
  public List<Boolean> solvedByIds(long userId, List<Long> problemIds) {
    List<Key<Solution>> keys = Lists.newArrayList();
    for (long problemId : problemIds) {
      String solutionId = Solution.createId(userId, problemId);
      keys.add(Key.create(Solution.class, solutionId));
    }
    Set<Long> foundSet = Sets.newHashSet();
    for (Solution solution : ObjectifyService.begin().get(keys).values()) {
      foundSet.add(solution.getProblemId());
    }
    List<Boolean> result = Lists.newArrayList();
    for (long problemId : problemIds) {
      result.add(foundSet.contains(problemId));
    }
    return result;
  }

  public List<Boolean> solvedByProblems(long userId, List<Problem> problems) {
    List<Long> problemIds = Lists.newArrayList();
    for (Problem problem : problems) {
      problemIds.add(problem.getId());
    }
    return solvedByIds(userId, problemIds);
  }

  public boolean solvedByProblem(long userId, Problem problem) {
    return solvedByProblems(userId, Lists.newArrayList(problem)).get(0);
  }
}
