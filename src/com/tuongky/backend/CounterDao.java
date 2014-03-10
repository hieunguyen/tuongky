package com.tuongky.backend;

import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.SimpleCounter;

/**
 * A class to manage increasing counters.
 *
 * Created by sngo on 2/9/14.
 */
public class CounterDao {

  static {
    ObjectifyRegister.register();
  }

  private static final String PROBLEM_ID_GENERATOR_STORE = "problemIdGenerator";

  private static final String PROBLEM_COUNT = "problemCount";
  private static final String USER_COUNT = "userCount";

  public static long increaseAndGet(String key) {
    Objectify ofy = ObjectifyService.beginTransaction();

    Transaction txn = ofy.getTxn();

    SimpleCounter counter = ofy.find(SimpleCounter.class, key);
    if (counter == null) {
      counter = new SimpleCounter(key);
    }
    counter.increase();
    long id = counter.getCounter();

    ofy.put(counter);

    txn.commit();

    return id;
  }

  public static long decreaseAndGet(String key) {
    Objectify ofy = ObjectifyService.beginTransaction();

    Transaction txn = ofy.getTxn();

    SimpleCounter counter = ofy.find(SimpleCounter.class, key);
    if (counter == null) {
      counter = new SimpleCounter(key);
    }
    counter.decrease();
    long id = counter.getCounter();

    ofy.put(counter);

    txn.commit();

    return id;
  }

  public static long getProblemsCount(){
    SimpleCounter counter = ObjectifyService.begin().find(SimpleCounter.class, PROBLEM_COUNT);
    if (counter == null){
      return 0;
    } else {
      return counter.getCounter();
    }
  }

  public static long getUsersCount(){
    SimpleCounter counter = ObjectifyService.begin().find(SimpleCounter.class, USER_COUNT);
    if (counter == null){
      return 0;
    } else {
      return counter.getCounter();
    }
  }

  public static long addProblem(){
    return increaseAndGet(PROBLEM_COUNT);
  }

  public static long subtractProblem(){
    return decreaseAndGet(PROBLEM_COUNT);
  }
  public static long addUser(){
    return increaseAndGet(USER_COUNT);
  }
  public static long subtractUser(){
    return decreaseAndGet(USER_COUNT);
  }

  // This method returns the next number in the sequence atomically
  public static long getNextAvailableProblemId(){
    return increaseAndGet(PROBLEM_ID_GENERATOR_STORE);
  }

  public static long getLastProblemId() {
    SimpleCounter counter =
        ObjectifyService.begin().find(SimpleCounter.class, PROBLEM_ID_GENERATOR_STORE);
    return counter == null ? 0 : counter.getCounter();
  }
}
