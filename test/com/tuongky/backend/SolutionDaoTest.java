package com.tuongky.backend;

import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.UserMetadata;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by sngo on 2/26/14.
 */
public class SolutionDaoTest extends BasedProblemTest {

  @Test
  public void testSolve() {
    ProblemAttempt attempt = updateService.attemptProblem(userId_1, problemId_2);
    updateService.solveProblem(attempt, "");

    List<Solution> solutions = SolutionDao.instance.searchByProblem(problemId_2, null, 0);
    assertEquals(solutions.size(), 1);

    // user_1 solve again
    updateService.solveProblem(attempt, "");
    solutions = SolutionDao.instance.searchByProblem(problemId_2, null, 0);
    assertEquals(solutions.size(), 1);

    // user_2 solve
    ProblemAttempt attempt2 = updateService.attemptProblem(userId_2, problemId_2);
    updateService.solveProblem(attempt2, "");
    solutions = SolutionDao.instance.searchByProblem(problemId_2, null, 0);
    assertEquals(solutions.size(), 2);

    solutions = SolutionDao.instance.searchByActor(userId_1, null, 0);
    assertEquals(solutions.size(), 1);

    UserMetadata userMetadata = UserMetadataDao.instance.getByUser(user1);
    assertEquals(1, userMetadata.getSolves());
    assertEquals(1, userMetadata.getAttempts());

    ProblemAttempt attempt3 = updateService.attemptProblem(userId_1, problemId_1);
    updateService.solveProblem(attempt3, "");
    userMetadata = UserMetadataDao.instance.getByUser(user1);
    assertEquals(2, userMetadata.getSolves());
  }
}
