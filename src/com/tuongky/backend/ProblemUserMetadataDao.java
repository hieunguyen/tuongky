package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.ProblemUserMetadata;

import java.util.*;

/**
 * Created by sngo on 2/23/14.
 */
public class ProblemUserMetadataDao {

  static {
    ObjectifyRegister.register();
  }

  public static final ProblemUserMetadataDao instance = new ProblemUserMetadataDao();

  public ProblemUserMetadata create(long userId, long problemId){
    ProblemUserMetadata problemUserMetadata = new ProblemUserMetadata(userId, problemId);

    ObjectifyService.begin().put(problemUserMetadata);

    return problemUserMetadata;
  }

  public int increaseAttempt(long userId,long problemId){
    String id = ProblemUserMetadata.createId(userId, problemId);

    Objectify ofy = ObjectifyService.beginTransaction();

    ProblemUserMetadata metadata = ofy.find(ProblemUserMetadata.class, id);

    int count;

    if (metadata != null){
      count = metadata.increaseAndGetAttempts();
    }else {
      metadata = new ProblemUserMetadata(userId, problemId);
      metadata.setAttempts(1);

      count = 1;
    }

    ofy.put(metadata);

    ofy.getTxn().commit();

    return count;
  }

  // Given a user and a list of ProblemId, return how many attempts he has made to solve these problems
  public Map<Long, Integer> findAttemptsByUser(long userId, Set<Long> problemIds){

    Set<String> idSet = new HashSet<>();
    for (long id : problemIds)
    {
      idSet.add(ProblemUserMetadata.createId(userId, id));
    }

    Map<String, ProblemUserMetadata> results = ObjectifyService.begin().get(ProblemUserMetadata.class, idSet);

    Map<Long, Integer> ret = new HashMap<>();
    for (ProblemUserMetadata metadata : results.values()) {
      ret.put(metadata.getProblemId(), metadata.getAttempts());
    }

    return ret;
  }

  // Given a problemId and a list of userId, return how many attempts they have made to solve this problem
  public Map<Long, Integer> findAttemptsByProblem(long problemId, Set<Long> userIds){

    Set<String> idSet = new HashSet<>();
    for (long id : userIds)
    {
      idSet.add(ProblemUserMetadata.createId(id, problemId));
    }

    Map<String, ProblemUserMetadata> results = ObjectifyService.begin().get(ProblemUserMetadata.class, idSet);

    Map<Long, Integer> ret = new HashMap<>();
    for (ProblemUserMetadata metadata : results.values()) {
      ret.put(metadata.getActorId(), metadata.getAttempts());
    }

    return ret;
  }

  public ProblemUserMetadata get(long actorId, long problemId){
    return ObjectifyService.begin().find(ProblemUserMetadata.class, ProblemUserMetadata.createId(actorId, problemId));
  }
}
