package com.tuongky.model.datastore;

import javax.persistence.Id;

/**
 * This object store the info: a[i]=the number of users who have solved at least i problems
 *
 * Created by sngo on 2/16/14.
 */
public class Ranker {
  private @Id Long id;

  private int count;

  @SuppressWarnings("unused")
  private Ranker(){
    // used by Objectify
  }

  public Ranker(long id) {
    this(id, 0);
  }

  public Ranker(long id, int count) {
    this.id = id;
    this.count = count;
  }
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void increaseCount(){
    count++;
  }
}
