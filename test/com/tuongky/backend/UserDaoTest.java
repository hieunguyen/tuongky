package com.tuongky.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;

/**
 * Created by sngo on 2/26/14.
 */
public class UserDaoTest extends BasedProblemTest {

  @Test
  public void testRanker() {
    ProblemAttempt attempt = updateService.attemptProblem(userId_1, problemId_1);
    ProblemAttempt attempt2 = updateService.attemptProblem(userId_1, problemId_2);
    updateService.solveProblem(attempt2, "");

    ProblemAttempt attempt3 = updateService.attemptProblem(userId_2, problemId_2);
    ProblemAttempt attempt4 = updateService.attemptProblem(userId_2, problemId_1);
    updateService.solveProblem(attempt4, "");

    List<UserMetadata> userList = UserMetadataDao.instance.search(0, 100);
    assertEquals(2, userList.size());
    assertEquals(userId_1, (long) userList.get(0).getId());
    assertEquals(userId_2, (long) userList.get(1).getId());

//    assertEquals(2, userList.get(0).getSolves());
//    assertEquals(1, userList.get(1).getSolves());
//    assertEquals(1, userList.get(2).getSolves());
//
//    assertEquals(1, UserRankerDao.instance.getRank(2));
//    assertEquals(3, UserRankerDao.instance.getRank(1));
//
//    // now user 1 solves 2 more problems
//    ProblemAttemptDao.instance.attempt(userId_1, problemId_3, false);
//    SolutionDao.instance.solve(userId_1, problemId_3);
//    SolutionDao.instance.solve(userId_1, problemId_1);
//
//    // Test ranking again
//    userList = UserMetadataDao.instance.search(0, 100);
//    assertEquals(3, userList.size());
//    assertEquals(userId_1, userList.get(0).getId());
//    assertEquals(userId_3, userList.get(1).getId());
//    assertEquals(userId_2, userList.get(2).getId());
//
//
//    assertEquals(3, userList.get(0).getSolves());
//    assertEquals(2, userList.get(1).getSolves());
//    assertEquals(1, userList.get(2).getSolves());
//
//    assertEquals(1, UserRankerDao.instance.getRank(3));
//    assertEquals(2, UserRankerDao.instance.getRank(2));
//    assertEquals(3, UserRankerDao.instance.getRank(1));
//
//    // Test ProblemUserMetadata
//    ProblemUserMetadata metadata = ProblemUserMetadataDao.instance.get(userId_1, problemId_2);
//    assertEquals(2, metadata.getAttempts());
  }
}
