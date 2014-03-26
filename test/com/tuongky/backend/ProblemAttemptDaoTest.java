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
  public void testFailAttempt() {
    ProblemAttempt attempt1 = updateService.attemptProblem(userId_1, problemId_1);
    ProblemAttempt attempt2 = updateService.attemptProblem(userId_2, problemId_1);
    UserMetadata userMetadata = UserMetadataDao.instance.getByUser(user1);

    assertNotNull(attempt1);
    assertNotNull(attempt2);

    // check #attempt increases
    assertEquals(0, ProblemDao.instance.getById(problemId_1).getSolvers());
    assertEquals(2, ProblemDao.instance.getById(problemId_1).getAttempters());

    assertFalse(attempt1.isSuccessful());
    assertFalse(attempt2.isSuccessful());

    // check get
    List<ProblemAttempt> list =
        ProblemAttemptDao.instance.searchByActor(user1, null, null, 0);
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
