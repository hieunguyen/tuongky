package com.tuongky.backend;

import java.util.List;

import javax.jdo.annotations.Transactional;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Ranker;
import com.tuongky.model.datastore.UserMetadata;

/**
 * Created by sngo on 2/16/14.
 */
public class UserRankerDao extends DAOBase{

  static {
    ObjectifyRegister.register();
  }

  public static final UserRankerDao instance = new UserRankerDao();

  // How many users who solves at least [solves] problems.
  // Note that if solves=0, it will return 0 since there is no instance with id=0 in the datastore
  public int getRank(int solves){
    Ranker ranker = ObjectifyService.begin().find(Ranker.class, solves);
    if (ranker == null){
      return 0;
    } else {
      return ranker.getCount();
    }
  }

  // Update the number of users who solved [solves] problem
  @Transactional
  public void increaseRank(int solves){
    Objectify ofy = ObjectifyService.beginTransaction();

    Ranker ranker = ofy.find(Ranker.class, solves);
    if (ranker == null){
      ranker = new Ranker(solves);
    }
    ranker.increaseCount();
    ofy.put(ranker);
    ofy.getTxn().commit();
  }

  private static final int MAX_NUM_PROBLEMS = 1000000;
  public void recomputeRanking(){
    List<UserMetadata> users = ObjectifyService.begin().query(UserMetadata.class).list();

    Objectify ofy = ObjectifyService.beginTransaction();

    int[] array = new int[MAX_NUM_PROBLEMS];

    int max = 0;
    for (UserMetadata user : users) {
      int x = user.getSolves();

      array[x]++;

      if (x > max){
        max = x;
      }
    }

    for (int i = max; i >= 0; i--){
      array[i] += array[i+1];
    }

    for (int i = 1; i <= max + 1 ; i++){
      Ranker ranker = new Ranker(i, array[i]);
      ofy.put(ranker);
    }

    ofy.getTxn().commit();
  }
}
