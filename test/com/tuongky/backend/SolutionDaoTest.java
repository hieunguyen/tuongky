package com.tuongky.backend;

import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.UserMetadata;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by sngo on 2/26/14.
 */
public class SolutionDaoTest extends BasedProblemTest{

  @Test
  public void testSolve(){
    ProblemAttemptDao.instance.attempt(userId_1, problemId_2, true);
    SolutionDao.instance.solve(userId_1, problemId_2);

    List<Solution> solution = SolutionDao.instance.searchByProblem(problemId_2, null, 0);
    assertEquals(solution.size(), 1);

    // user_1 solve again
    SolutionDao.instance.solve(userId_1, problemId_2);
    solution = SolutionDao.instance.searchByProblem(problemId_2, null, 0);
    assertEquals(solution.size(), 1);

    // user_2 solve
    SolutionDao.instance.solve(userId_2, problemId_2);
    solution = SolutionDao.instance.searchByProblem(problemId_2, null, 0);
    assertEquals(solution.size(), 2);

    solution = SolutionDao.instance.searchByActor(userId_1, null, 0);
    assertEquals(solution.size(), 1);

    UserMetadata userMetadata = UserMetadataDao.instance.get(userId_1);
    assertEquals(1, userMetadata.getSolves());
    assertEquals(1, userMetadata.getAttempts());

    ProblemAttemptDao.instance.attempt(userId_1, problemId_1, true);
    SolutionDao.instance.solve(userId_1, problemId_1);
    userMetadata = UserMetadataDao.instance.get(userId_1);
    assertEquals(2, userMetadata.getSolves());
  }
}
