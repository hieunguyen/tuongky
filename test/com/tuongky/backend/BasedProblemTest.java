package com.tuongky.backend;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.User;
import org.junit.After;
import org.junit.Before;

/**
 * Created by sngo on 2/26/14.
 */
public class BasedProblemTest {

//  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
          .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

  protected long userId_1;
  protected long userId_2;

  protected long problemId_1;
  protected long problemId_2;

  @Before
  public void init(){
    helper.setUp();

    String fen = "fen";
    String title = "title";
    String desc = "desc";
    String requirement = "req";

    problemId_1 = ProblemDao.instance.create(fen, title, desc, requirement, null);
    problemId_2 = ProblemDao.instance.create(fen, title, desc, requirement, null);

    User user = UserDao.instance.save("fb", "fb", null, UserRole.USER);
    userId_1 = user.getId();
    user = UserDao.instance.save("fb1", "fb2", null, UserRole.USER);
    userId_2 = user.getId();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
