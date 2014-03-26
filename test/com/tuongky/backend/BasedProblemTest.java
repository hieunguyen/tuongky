package com.tuongky.backend;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.User;
import com.tuongky.service.UpdateService;

/**
 * Created by sngo on 2/26/14.
 */
public class BasedProblemTest {

  // Change this to false to test eventual consistency behavior whenever needed.
  protected static boolean SHOULD_APPLY = true;

  private static final class CustomHighRepJobPolicy implements HighRepJobPolicy {
    @Override
    public boolean shouldApplyNewJob(Key arg0) {
      return SHOULD_APPLY;
    }

    @Override
    public boolean shouldRollForwardExistingJob(Key arg0) {
      return SHOULD_APPLY;
    }
  }

//  private final LocalServiceTestHelper helper =
//      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

//  private final LocalServiceTestHelper helper =
//      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
//          .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
          .setAlternateHighRepJobPolicyClass(CustomHighRepJobPolicy.class));

  protected static final UpdateService updateService = DatastoreUpdateService.instance;

  protected User user1;
  protected User user2;

  protected Problem problem1;
  protected Problem problem2;

  protected long userId_1, userId_2;
  protected long problemId_1, problemId_2;

  @Before
  public void init() {
    helper.setUp();

    String fen = "fen";
    String title = "title";
    String desc = "desc";
    String requirement = "req";

    problem1 = new Problem(null, title, fen, desc, requirement, null);
    problem2 = new Problem(null, title, fen, desc, requirement, null);

    problem1 = updateService.createProblem(problem1);
    problem2 = updateService.createProblem(problem2);

    user1 = updateService.saveFacebookUser("12", "fb1", null, UserRole.USER);
    user2 = updateService.saveFacebookUser("34", "fb2", null, UserRole.ADMIN);

    userId_1 = user1.getId();
    userId_2 = user2.getId();
    problemId_1 = problem1.getId();
    problemId_2 = problem2.getId();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCount() {
    assertEquals(2, CounterDao.getProblemsCount());
    assertEquals(2, CounterDao.getUsersCount());
  }
}
