package com.tuongky.backend;

import java.util.List;

import com.tuongky.model.datastore.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sngo on 2/25/14.
 */
public class ProblemAttemptDaoTest extends BasedProblemTest {

  @Test
  public void testFailAttempt(){
    String attemptId = ProblemAttemptDao.instance.attempt(userId_1, problemId_1, false);
    ProblemAttemptDao.instance.attempt(userId_2, problemId_1, false);

    Problem problem = ProblemDao.instance.getById(problemId_1);
    UserMetadata userMetadata = UserMetadataDao.instance.get(userId_1);

    ProblemAttempt problemAttempt = ProblemAttemptDao.instance.getById(attemptId);

    assertNotNull(problem);
    assertNotNull(userMetadata);
    assertNotNull(problemAttempt);

    // check #attempt increases
    assertEquals(0, problem.getSolvers());
    assertEquals(2, problem.getAttempters());

    assertFalse(problemAttempt.isSuccessful());
    // check get
    List<ProblemAttempt> list = ProblemAttemptDao.instance.searchByActor(userId_1, null, null, 0);
    assertNotNull(list);
    assertEquals(list.size(), 1);
    assertEquals(list.get(0).getActorId(), userId_1);
    assertEquals(list.get(0).getProblemId(), problemId_1);

    list = ProblemAttemptDao.instance.searchByProblem(problemId_1, null, null, 0);
    assertNotNull(list);
    assertEquals(list.size(), 2);
    assertEquals(list.get(0).getActorId(), userId_2);
    assertEquals(list.get(0).getProblemId(), problemId_1);

    assertEquals(list.get(1).getActorId(), userId_1);
    assertEquals(list.get(1).getProblemId(), problemId_1);
  }

}
