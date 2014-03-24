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
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;

/**
 * Created by sngo on 2/26/14.
 */
public class UserDaoTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

//  private final LocalServiceTestHelper helper =
//      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
//          .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

  protected long userId_1;
  protected long userId_2;
  protected long userId_3;

  protected long problemId_1;
  protected long problemId_2;
  protected long problemId_3;

  String fbName = "fbName";
  String fbId = "12345";

  @Before
  public void setUp() {
    helper.setUp();

    String fen = "fen";
    String title = "title";
    String desc = "desc";
    String requirement = "req";

    problemId_1 = ProblemDao.instance.create(fen, title, desc, requirement, null);
    problemId_2 = ProblemDao.instance.create(fen, title, desc, requirement, null);
    problemId_3 = ProblemDao.instance.create(fen, title, desc, requirement, null);
  }

  @Test
  public void testUser() {
    UserDao.instance.saveFbUser(fbId, fbName, null, UserRole.USER).getId();
    User user = UserDao.instance.getByFbId(fbId);
    assertEquals(fbId, user.getFbId());
    assertEquals(fbName, user.getFbName());
    User new_user = UserDao.instance.saveFbUser(fbId, fbName, null, UserRole.ADMIN);
    assertEquals(user.getId(), new_user.getId());
    assertEquals(UserRole.ADMIN, new_user.getUserRole());

    UserMetadata userMetadata = UserMetadataDao.instance.getByUser(user);
    assertEquals(userMetadata.getId(), (long) user.getId());
    System.out.println(userMetadata.getUserKey());
    System.out.println(userMetadata.getUserKey().getParent());
    System.out.println(user.getExternalUserKey());

    System.out.println(UserMetadataDao.instance.getAllUserMetadatas().size());
  }

//  @Test
  public void testRanker() {
    userId_1 = UserDao.instance.saveFbUser(fbId, fbName, null, UserRole.USER).getId();
    userId_2 = UserDao.instance.saveFbUser("fbId1", "fb2", null, UserRole.USER).getId();
    userId_3 = UserDao.instance.saveFbUser("fbId2", "fb2", null, UserRole.USER).getId();
    // user_1 solve 1, attempt 3
    // user 2 solve 1, attempt 2
    // user 3 solve 2, attempt 2
    ProblemAttemptDao.instance.attempt(userId_1, problemId_1, false);
    ProblemAttemptDao.instance.attempt(userId_1, problemId_2, false);
    ProblemAttemptDao.instance.attempt(userId_1, problemId_2, true);
    SolutionDao.instance.solve(userId_1, problemId_2);

    ProblemAttemptDao.instance.attempt(userId_2, problemId_2, false);
    ProblemAttemptDao.instance.attempt(userId_2, problemId_1, true);
    SolutionDao.instance.solve(userId_2, problemId_1);

    ProblemAttemptDao.instance.attempt(userId_3, problemId_2, true);
    ProblemAttemptDao.instance.attempt(userId_3, problemId_3, true);
    SolutionDao.instance.solve(userId_3, problemId_2);
    SolutionDao.instance.solve(userId_3, problemId_3);

    List<UserMetadata> userList = UserMetadataDao.instance.search(0, 100);
    assertEquals(3, userList.size());
    assertEquals(userId_3, userList.get(0).getId());
    assertEquals(userId_2, userList.get(1).getId());
    assertEquals(userId_1, userList.get(2).getId());


    assertEquals(2, userList.get(0).getSolves());
    assertEquals(1, userList.get(1).getSolves());
    assertEquals(1, userList.get(2).getSolves());

    assertEquals(1, UserRankerDao.instance.getRank(2));
    assertEquals(3, UserRankerDao.instance.getRank(1));

    // now user 1 solves 2 more problems
    ProblemAttemptDao.instance.attempt(userId_1, problemId_3, false);
    SolutionDao.instance.solve(userId_1, problemId_3);
    SolutionDao.instance.solve(userId_1, problemId_1);

    // Test ranking again
    userList = UserMetadataDao.instance.search(0, 100);
    assertEquals(3, userList.size());
    assertEquals(userId_1, userList.get(0).getId());
    assertEquals(userId_3, userList.get(1).getId());
    assertEquals(userId_2, userList.get(2).getId());


    assertEquals(3, userList.get(0).getSolves());
    assertEquals(2, userList.get(1).getSolves());
    assertEquals(1, userList.get(2).getSolves());

    assertEquals(1, UserRankerDao.instance.getRank(3));
    assertEquals(2, UserRankerDao.instance.getRank(2));
    assertEquals(3, UserRankerDao.instance.getRank(1));

    // Test ProblemUserMetadata
    ProblemUserMetadata metadata = ProblemUserMetadataDao.instance.get(userId_1, problemId_2);
    assertEquals(2, metadata.getAttempts());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
