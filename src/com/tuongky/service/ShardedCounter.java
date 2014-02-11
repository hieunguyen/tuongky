package com.tuongky.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Unindexed;

public class ShardedCounter implements Serializable {

  static {
    ObjectifyService.register(EntityCounter.class);
    ObjectifyService.register(EntityCounterShard.class);
  }

  private static final long serialVersionUID = 1L;

  @Transient
  private transient ObjectifyFactory of;
  @Unindexed
  private String name;

  private ShardedCounter() {
    of = ObjectifyService.factory();
  }

  private ShardedCounter(String name) {
    this();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void increment() {
    increment(1);
  }

  public void increment(int add) {
    Objectify ofy = of.begin();
    // fetch the counter to determine the number of shards
    EntityCounter counter = ofy.get(new Key<EntityCounter>(EntityCounter.class,
        name));

    // pick a random shard
    Random generator = new Random();
    int shardNum = generator.nextInt(counter.numShards);

    // getById the shard from the datastore, increment its value by 'add' and
    // persist it if the shard was modified in the datastore between the getById
    // and the persist, retry the operation
    Objectify trans = of.beginTransaction();
    int triesLeft = 3;
    while (true) {
      try {

        EntityCounterShard shard = trans.get(new Key<EntityCounterShard>(
            EntityCounterShard.class, name + shardNum));

        shard.value += add;

        trans.put(shard);
        trans.getTxn().commit();
        break;
      } catch (ConcurrentModificationException e) {
        if (triesLeft == 0) {
          throw e;
        }
        --triesLeft;
      } finally {
        if (trans.getTxn().isActive()) {
          trans.getTxn().rollback();
        }
      }
    }
  }

  public void decrement() {
    decrement(1);
  }

  public void decrement(int subs) {
    increment(subs * -1);
  }

  public void addShard() {
    addShards(1);
  }

  public void addShards(int newShards) {
    Objectify counterTrans = of.beginTransaction();
    int nextShardNumber = -1;
    EntityCounter counter = null;

    // fetch the counter to determine the number of existing shards
    // and increment the shard count
    int tries = 3;
    while (true) {
      try {
        counter = counterTrans.get(new Key<EntityCounter>(EntityCounter.class,
            name));
        nextShardNumber = counter.numShards;
        counter.numShards += newShards;
        counterTrans.put(counter);
        counterTrans.getTxn().commit();
      } catch (ConcurrentModificationException e) {
        if (tries == 0) {
          throw e;
        }
        --tries;
      } finally {
        if (counterTrans.getTxn().isActive()) {
          counterTrans.getTxn().rollback();
        }
      }
      break;
    }

    // by increasing counter.numShards, this thread reserved
    // a shard 'range', so this thread is the only one
    // that could add shards with the shard numbers we're about to add:

    // add newShard number shards
    int shardsAdded = 0;
    Objectify ofy = of.begin();
    while (shardsAdded < newShards) {
      EntityCounterShard newShard = new EntityCounterShard(counter,
          nextShardNumber);
      ofy.put(newShard);
      shardsAdded++;
      nextShardNumber++;
    }
  }

  public int getCount() {
    Objectify ofy = of.begin();
    EntityCounter counter = ofy.get(new Key<EntityCounter>(EntityCounter.class,
        name));

    List<Key<EntityCounterShard>> shardKeys = new ArrayList<Key<EntityCounterShard>>();
    for (int shard = 0; shard < counter.numShards; shard++) {
      shardKeys.add(new Key<EntityCounterShard>(EntityCounterShard.class,
          String.format("%s%d", name, shard)));
    }

    Collection<EntityCounterShard> shards = ofy.get(shardKeys).values();
    int count = 0;
    for (EntityCounterShard shard : shards) {
      count += shard.value;
    }

    return count;
  }

  public static ShardedCounter getOrCreateCounter(String name, int numShards) {
    ShardedCounter shardedCounter = new ShardedCounter(name);
    Objectify trans = ObjectifyService.beginTransaction();
    int tries = 3;
    while (true) {
      try {
        EntityCounter counter = trans.find(new Key<EntityCounter>(
            EntityCounter.class, name));
        if (counter == null) {
          // create new counter
          counter = new EntityCounter(name);
          trans.put(counter);
          trans.getTxn().commit();
          shardedCounter.addShards(numShards);
        }

        break;
      } catch (ConcurrentModificationException e) {
        if (tries == 0) {
          throw e;
        }
        --tries;
      } finally {
        if (trans.getTxn().isActive()) {
          trans.getTxn().rollback();
        }
      }
    }
    return shardedCounter;
  }

  public static ShardedCounter createCounter(String name, int numShards) {
    ShardedCounter shardedCounter = new ShardedCounter(name);
    Objectify trans = ObjectifyService.beginTransaction();
    int tries = 3;
    while (true) {
      try {
        EntityCounter counter = trans.find(new Key<EntityCounter>(
            EntityCounter.class, name));
        if (counter == null) {
          // create new counter
          counter = new EntityCounter(name);
          trans.put(counter);
          trans.getTxn().commit();
          shardedCounter.addShards(numShards);
        } else {
          throw new IllegalArgumentException("A counter with name " + name
              + " does already exist!");
        }

        break;
      } catch (ConcurrentModificationException e) {
        if (tries == 0) {
          throw e;
        }
        --tries;
      } finally {
        if (trans.getTxn().isActive()) {
          trans.getTxn().rollback();
        }
      }
    }
    return shardedCounter;
  }

  public static ShardedCounter getCounter(String name) {
    Objectify ofy = ObjectifyService.begin();
    if (ofy.find(new Key<EntityCounter>(EntityCounter.class, name)) != null) {
      return new ShardedCounter(name);
    }
    return null;
  }

  /**
   * EntityCounter is stored in the data store to reserve a counter name and
   * remember how many shards it has.
   *
   */
  public static class EntityCounter {
    @Id
    public String name;
    public int numShards;

    @SuppressWarnings("unused")
    private EntityCounter() {
    }

    public EntityCounter(String name) {
      this.name = name;
    }

    public Key<EntityCounterShard> keyForShard(int shardNumber) {
      return new Key<EntityCounterShard>(EntityCounterShard.class,
          shardName(shardNumber));
    }

    public String shardName(int shardNumber) {
      return String.format("%s%d", name, shardNumber);
    }
  }

  /**
   * EntityCounterShard represents one shard and stores the result of a fraction
   * of the increment and decrement actions as its value.
   */
  public static class EntityCounterShard {
    /**
     * id is equal to the counter name appended with the number of the shard
     */
    @Id
    public String id;

    @Unindexed
    public int value;

    @SuppressWarnings("unused")
    private EntityCounterShard() {
    }

    public EntityCounterShard(EntityCounter counter, int shardNumber) {
      id = counter.shardName(shardNumber);
    }
  }
}
