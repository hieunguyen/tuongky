package com.tuongky.backend;

import java.util.List;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Problem;
import java.util.List;

/**
 * Created by sngo on 2/3/14.
 */
public class ProblemDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  private final String COUNTER_STORE = "problemCounter";
  public static ProblemDao instance = new ProblemDao();

  public Problem getById(long problemId) {
    return ObjectifyService.begin().get(Problem.class, problemId);
  }

  public long create(String fen, String title, String description, String requirement, Long creatorId) {
    long id = DistributedSequenceGenerator.increaseAndGet(COUNTER_STORE);

    Problem problem = new Problem(id, title, fen, description, requirement, creatorId);
    ObjectifyService.begin().put(problem);

    return id;
  }

  public void delete(long problemId) {
    ObjectifyService.begin().delete(Problem.class, problemId);
  }

  public List<Problem> findByCreator(long creatorId) {
    Objectify ofy = ObjectifyService.begin();

    Query<Problem> result = ofy.query(Problem.class).filter(Problem.CREATOR_FIELD, creatorId);
    return result == null ? null : Lists.newArrayList(result.iterator());
  }

  public int solvers(long problemId) {
    Objectify ofy = ObjectifyService.begin();

    Problem problem = ofy.find(Problem.class, problemId);

    return problem.getSolvers();
  }

  // Index starts from 0
  // if pageSize == null, getById default value
  public List<Problem> search(int offset, int limit) {
    Objectify ofy = ObjectifyService.begin();

    Query<Problem> query = ofy.query(Problem.class).order(Problem.ID_FIELD).limit(limit).offset(offset);

    return query.list();
  }

  // return -1 if problemId is not found
  //transactional
  public int addSolver(long problemId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    Transaction txn = ofy.getTxn();

    Problem problem = ofy.find(Problem.class, problemId);

    if (problem == null) {
      return -1;
    }

    int solvers = problem.addSolver();

    ofy.put(problem);
    txn.commit();

    return solvers;
  }

  // return -1 if problemId is not found
  //transactional
  public int addAttempter(long problemId) {
    Objectify ofy = ObjectifyService.beginTransaction();

    Transaction txn = ofy.getTxn();

    Problem problem = ofy.find(Problem.class, problemId);

    if (problem == null) {
      return -1;
    }

    int attempters = problem.addAttempter();

    ofy.put(problem);
    txn.commit();

    return attempters;
  }
}
