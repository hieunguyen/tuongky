package com.tuongky.backend;

import static org.junit.Assert.assertEquals;

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

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  protected long userId_1;
  protected long userId_2;
  protected long userId_3;

  protected long problemId_1;
  protected long problemId_2;
  protected long problemId_3;

  String fbName = "fbName";
  String fbId = "12345";

  @Before
  public void setUp(){
    helper.setUp();

    String fen = "fen";
    String title = "title";
    String desc = "desc";
    String requirement = "req";

    problemId_1 = ProblemDao.instance.create(fen, title, desc, requirement, null);
    problemId_2 = ProblemDao.instance.create(fen, title, desc, requirement, null);
    problemId_3 = ProblemDao.instance.create(fen, title, desc, requirement, null);

    userId_1 = UserDao.instance.save(fbId, fbName, null, UserRole.USER).getId();
    userId_2 = UserDao.instance.save("fbId1", "fb2", null, UserRole.USER).getId();
    userId_3 = UserDao.instance.save("fbId2", "fb2", null, UserRole.USER).getId();
  }

  @Test
  public void testUser(){
    User user = UserDao.instance.getById(userId_1);
    assertEquals(fbId, user.getFbId());
    assertEquals(fbName, user.getFbName());

    user = UserDao.instance.getByFbId(fbId);
    assertEquals(userId_1, user.getId().longValue());

    User new_user = UserDao.instance.save(fbId, fbName, null, UserRole.ADMIN);
    assertEquals(userId_1, new_user.getId().longValue());
    assertEquals(UserRole.ADMIN, new_user.getUserRole());
  }

  @Test
  public void testRanker(){
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
  public void tearDown(){
    helper.tearDown();
  }
}
