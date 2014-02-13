package com.tuongky.backend;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.*;
import com.tuongky.util.ProblemUtils;

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
}
