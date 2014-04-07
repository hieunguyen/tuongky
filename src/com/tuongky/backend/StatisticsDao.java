package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemMetadata;
import com.tuongky.model.datastore.Statistics;

import java.util.List;

/**
 * Created by sngo on 4/6/14.
 */
public class StatisticsDao extends DAOBase {
  static {
    ObjectifyRegister.register();
  }

  public static StatisticsDao instance = new StatisticsDao();

  public void put() {
    long users = CounterDao.getUsersCount();
    long views  = 0;

    List<ProblemMetadata> problemMeta = ProblemMetadataDao.instance.getAll();

    for (ProblemMetadata problem : problemMeta) {
      views += problem.getViews();
    }

    List<Problem> problems = ProblemDao.instance.getAll();

    long solves = 0;
    long attempts = 0;

    for (Problem problem : problems) {
      solves += problem.getSolvers();
      attempts += problem.getAttempters();
    }

    ObjectifyService.begin().put(new Statistics(users, views, solves, attempts));
  }
}
