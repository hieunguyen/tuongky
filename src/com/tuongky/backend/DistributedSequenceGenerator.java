package com.tuongky.backend;

import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.SimpleCounter;

/**
 * Created by sngo on 2/9/14.
 */
public class DistributedSequenceGenerator {

  static {
    ObjectifyRegister.register();
  }

  // This method returns the next number in the sequence atomically

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

}
