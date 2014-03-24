package com.tuongky.service;

import javax.annotation.Nullable;

import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;

public interface UpdateService {

  User saveFacebookUser(String fbId, String fbName, @Nullable String email, UserRole role);

  Problem createProblem(Problem problem);

  Problem saveProblem(Problem problem);

  void deleteProblem(long problemId);

  ProblemAttempt attemptProblem(long userId, long problemId);

  Solution solveProblem(long userId, long problemId, ProblemAttempt attempt);
}
