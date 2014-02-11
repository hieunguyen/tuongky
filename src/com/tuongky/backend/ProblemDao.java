package com.tuongky.backend;

import java.util.List;

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Problem;

/**
 * Created by sngo on 2/3/14.
 */
public class ProblemDao extends DAOBase {

  static {
    ObjectifyService.register(Problem.class);
  }

  private final String COUNTER_STORE = "problemCounter";
  public static ProblemDao instance = new ProblemDao();

  private static int PAGE_SIZE_DEFAULT = 20;

  static {
    ObjectifyService.register(Problem.class);
  }

  public Problem getById(long problemId) {
    return ObjectifyService.begin().get(Problem.class, problemId);
  }

  public long save(String fen, String title, String description, String requirement) {
    long id = DistributedSequenceGenerator.increaseAndGet(COUNTER_STORE);

    Problem problem = new Problem(id, title, fen, description, requirement);
    ObjectifyService.begin().put(problem);

    return id;
  }

  public void delete(String problemId) {
    ObjectifyService.begin().delete(problemId);
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
  public List<Problem> search(Integer pageSize, int pageNum) {
    Objectify ofy = ObjectifyService.begin();

    int startIndex = pageNum * pageSize;

    int count = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;

    Query<Problem> query = ofy.query(Problem.class).order(Problem.ID_FIELD).limit(startIndex).offset(count);
    List<Problem> list = Lists.newArrayList(query.iterator());

    return list;
  }

  // return -1 if problemId is not found
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
