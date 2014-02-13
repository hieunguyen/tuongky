package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
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

  private static final Logger log = Logger.getLogger(Solution.class.getName());

  // transactional
  public String solve(long actorId, long problemId) {

    Solution solution = (Solution) ProblemUtils.newProblemAttempt(actorId, problemId, null);

    Objectify ofy = ObjectifyService.beginTransaction();

    Query<Solution> result = ofy.query(Solution.class).filter(Solution.ID_FIELD, solution.getId());

    if (result == null) {
      if (ProblemDao.instance.addSolver(problemId) == -1) {
        log.warning("Fail to add attempter to problem " + problemId);
      }
    }

    ofy.put(solution);

    UserMetadataDao.instance.solve(actorId, ofy);

    ofy.getTxn().commit();
    return solution.getId();
  }
}
