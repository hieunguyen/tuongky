package com.tuongky.backend;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.tuongky.model.UserRole;
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
  public void testSaveFacebookUser() {
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
    assertEquals(metadata1 .getId(), user1.getId());
    UserMetadata metadata4 = UserMetadataDao.instance.getByUser(user4);
    assertEquals(metadata4 .getId(), user4.getId());
    assertEquals(2, UserMetadataDao.instance.getAllUserMetadatas().size());
  }
}
