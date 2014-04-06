package com.tuongky.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.service.UpdateService;

public class DatastoreUpdateServiceTest {

  private static final String FB_ID = "12345";
  private static final String FB_NAME = "Cho Vui";
  private static final String FB_ID_2 = "56789";
  private static final String FB_NAME_2 = "Cho Vui 2";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
          .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

  private static final UpdateService updateService = DatastoreUpdateService.instance;

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void saveFacebookUser() {
    User user1 = updateService.saveFacebookUser(FB_ID, FB_NAME, null, UserRole.USER);
    User user2 = updateService.saveFacebookUser(FB_ID, FB_NAME, null, UserRole.MODERATOR);
    User user3 = UserDao.instance.getById(user1.getId());
    assertEquals(user1.getId(), user3.getId());
    assertEquals(1, CounterDao.getUsersCount());
    User user4 = updateService.saveFacebookUser(FB_ID_2, FB_NAME_2, null, UserRole.USER);
    assertEquals(2, CounterDao.getUsersCount());
    User user5 = updateService.saveFacebookUser(FB_ID_2, FB_NAME_2, null, UserRole.ADMIN);
    assertEquals(2, CounterDao.getUsersCount());
    UserMetadata metadata1 = UserMetadataDao.instance.getByUser(user1);
    assertEquals(metadata1.getId(), user1.getId());
    UserMetadata metadata4 = UserMetadataDao.instance.getByUser(user4);
    assertEquals(metadata4.getId(), user4.getId());
    assertEquals(2, UserMetadataDao.instance.getAllUserMetadatas().size());
  }

  @Test
  public void createProblem() {
    User user = updateService.saveFacebookUser(FB_ID, FB_NAME, null, UserRole.USER);
    Problem p1 = new Problem(null, "title", "fen", "desc", "requirement", user.getId());
    p1 = updateService.createProblem(p1);
    assertEquals(1, CounterDao.getProblemsCount());
    assertEquals(2, CounterDao.getNextAvailableProblemId());
  }

  @Test
  public void deleteProblem() {
    User user = updateService.saveFacebookUser(FB_ID, FB_NAME, null, UserRole.USER);
    Problem p1 = new Problem(null, "title", "fen", "desc", "requirement", user.getId());
    p1 = updateService.createProblem(p1);
    assertEquals(1, CounterDao.getProblemsCount());
    assertEquals(2, CounterDao.getNextAvailableProblemId());
    updateService.deleteProblem(p1.getId());
    assertEquals(0, CounterDao.getProblemsCount());
  }

  @Test
  public void attemptProblem() {
    User user = updateService.saveFacebookUser(FB_ID, FB_NAME, null, UserRole.USER);
    Problem problem = new Problem(null, "title", "fen", "desc", "requirement", user.getId());
    problem = updateService.createProblem(problem);
    updateService.attemptProblem(user.getId(), problem.getId());
    Problem newProblem = ProblemDao.instance.getById(problem.getId());
    assertEquals(0, problem.getAttempters());
    assertEquals(1, newProblem.getAttempters());
    UserMetadata metadata = UserMetadataDao.instance.getByUser(user);
    assertEquals(1, metadata.getAttempts());
    ProblemUserMetadata puMetadata =
        ProblemUserMetadataDao.instance.getByUserAndProblem(user, problem.getId());
    assertEquals(1, puMetadata.getAttempts());
  }

  @Test
  public void solveProblem() {
    User user = updateService.saveFacebookUser(FB_ID, FB_NAME, null, UserRole.USER);
    Problem problem = new Problem(null, "title", "fen", "desc", "requirement", user.getId());
    assertEquals(0, CounterDao.getProblemsCount());
    problem = updateService.createProblem(problem);
    assertEquals(1, CounterDao.getProblemsCount());
    ProblemAttempt attempt = updateService.attemptProblem(user.getId(), problem.getId());
    assertFalse(attempt.isSuccessful());
    Solution solution = updateService.solveProblem(attempt, "");
    ProblemAttempt newAttempt = ProblemAttemptDao.instance.getById(user.getId(), attempt.getId());
    assertTrue(newAttempt.isSuccessful());
    UserMetadata metadata = UserMetadataDao.instance.getByUser(user);
    assertEquals(1, metadata.getSolves());
    int rank = UserRankerDao.instance.getRank(metadata.getSolves());
    assertEquals(1, rank);

    Problem problem2 = new Problem(null, "title", "fen", "desc", "requirement", user.getId());
    problem2 = updateService.createProblem(problem2);
    assertEquals(2, CounterDao.getProblemsCount());
    ProblemAttempt attempt2 = updateService.attemptProblem(user.getId(), problem2.getId());
    assertFalse(attempt2.isSuccessful());
    Solution solution2 = updateService.solveProblem(attempt2, "");
    ProblemAttempt newAttempt2 = ProblemAttemptDao.instance.getById(user.getId(), attempt2.getId());
    assertTrue(newAttempt2.isSuccessful());
    UserMetadata metadata2 = UserMetadataDao.instance.getByUser(user);
    assertEquals(2, metadata2.getAttempts());
    assertEquals(2, metadata2.getSolves());
    int rank1 = UserRankerDao.instance.getRank(1);
    int rank2 = UserRankerDao.instance.getRank(2);
    assertEquals(1, rank1);
    assertEquals(1, rank2);
  }
}
