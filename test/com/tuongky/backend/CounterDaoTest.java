package com.tuongky.backend;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.tuongky.backend.CounterDao;
import com.tuongky.backend.ProblemDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by sngo on 2/26/14.
 */
public class CounterDaoTest {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void init(){
    helper.setUp();
  }

  @Test
  public void testProblem(){
    String fen = "fen";
    String title = "title";
    String desc = "desc";
    String requirement = "req";

    long problemId_1 = ProblemDao.instance.create(fen, title, requirement, desc, null);
    long problemId_2 = ProblemDao.instance.create(fen, title, requirement, desc, null);

    assertEquals(2, CounterDao.getProblemsCount());
    assertEquals(1, problemId_2 - problemId_1);

    ProblemDao.instance.delete(problemId_1);
    assertEquals(1, CounterDao.getProblemsCount());
  }

  @Test
  public void testUser(){

    UserDao.instance.save("fb", "fb");
    UserDao.instance.save("fb", "fb").getId();

    assertEquals(2, CounterDao.getUsersCount());
  }


  @After
  public void tearDown() {
    helper.tearDown();
  }
}
