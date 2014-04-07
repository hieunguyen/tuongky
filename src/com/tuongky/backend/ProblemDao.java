package com.tuongky.backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.Solution;

/**
 * Created by sngo on 2/3/14.
 */
public class ProblemDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  public static ProblemDao instance = new ProblemDao();

  public Problem getById(long problemId) {
    return ObjectifyService.begin().find(Problem.class, problemId);
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
  public List<Problem> search(int offset, int limit, String order) {
    Objectify ofy = ObjectifyService.begin();

    Query<Problem> query = ofy.query(Problem.class).order(order).limit(limit).offset(offset);

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

  // brute-force, since #problems is small.
  public Problem getNextUnsolved(@Nullable Long userId, long problemId) {
    long lastProblemId = CounterDao.getLastProblemId();
    if (lastProblemId == 0) {
      return null;
    }
    long next = problemId + 1;

    List<Solution> solutions = Lists.newArrayList();
    if (userId != null) {
      solutions = SolutionDao.instance.searchByActor(userId, Integer.MAX_VALUE, 0);
    }
    Set<Long> solvedProblemSet = new HashSet<>();
    for (Solution solution : solutions) {
      solvedProblemSet.add(solution.getProblemId());
    }

    int cnt = 0;
    Problem problem = null;
    while (next != problemId && cnt < 1000) {
      if (next > lastProblemId) {
        next = 1;
      }
      if (!solvedProblemSet.contains(next)) {
        problem = ProblemDao.instance.getById(next);
        if (problem != null) {
          return problem;
        }
      }

      next++;
      cnt++;
    }
    return problem;
  }

  public List<Problem> getAll() {
    return ObjectifyService.begin().query(Problem.class).list();
  }
}
