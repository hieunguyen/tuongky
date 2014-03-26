package com.tuongky.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.User;

/**
 * Created by sngo on 2/23/14.
 */
public class ProblemUserMetadataDao {

  static {
    ObjectifyRegister.register();
  }

  public static ProblemUserMetadataDao instance = new ProblemUserMetadataDao();

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

  public ProblemUserMetadata getByUserAndProblem(User user, long problemId, Objectify ofy) {
    String id = ProblemUserMetadata.createId(user.getId(), problemId);
    Key<ProblemUserMetadata> key = Key.create(user.createKey(), ProblemUserMetadata.class, id);
    return ofy.find(key);
  }

  public ProblemUserMetadata getByUserAndProblem(User user, long problemId) {
    return getByUserAndProblem(user, problemId, ObjectifyService.begin());
  }
}
