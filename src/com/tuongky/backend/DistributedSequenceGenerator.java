package com.tuongky.backend;

import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.service.ShardedCounter;

/**
 * Created by sngo on 2/9/14.
 */
public class DistributedSequenceGenerator {

  // This method returns the next number in the sequence atomically
  public static int increaseAndGet(String key) {
    Objectify ofy = ObjectifyService.beginTransaction();

    Transaction txn = ofy.getTxn();

    ShardedCounter counter = ofy.find(ShardedCounter.class, key);
    if (counter == null) {
      counter = ShardedCounter.createCounter(key, 5);
    }
    counter.increment();
    int id = counter.getCount();

    ofy.put(counter, key);

    txn.commit();

    return id;
  }

}
