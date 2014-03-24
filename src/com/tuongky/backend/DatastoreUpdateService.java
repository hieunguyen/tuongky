package com.tuongky.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.ExternalUser;
import com.tuongky.model.datastore.Problem;
import com.tuongky.model.datastore.ProblemAttempt;
import com.tuongky.model.datastore.ProblemUserMetadata;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.service.UpdateService;
import com.tuongky.service.email.EmailTaskQueueService;

public class DatastoreUpdateService implements UpdateService {

  public static DatastoreUpdateService instance = new DatastoreUpdateService();

  private boolean createIfNewFbUser(String fbId) {
    String id = ExternalUser.createIdFromFbId(fbId);
    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      ExternalUser facebookUser = ExternalUserDao.instance.getById(id, ofy);
      if (facebookUser == null) {
        ExternalUserDao.instance.save(id, ofy);
        ofy.getTxn().commit();
        return true;
      }
      return false;
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }

  private boolean hasChanges(User user, String fbName, String email, UserRole role) {
    if (!user.getFbName().equals(fbName)) {
      return true;
    }
    if (email != null && !email.equals(user.getEmail())) {
      return true;
    }
    if (user.getUserRole() != role) {
      return true;
    }
    return false;
  }

  @Override
  public User saveFacebookUser(String fbId, String fbName, String email, UserRole role) {
    boolean newFbUser = createIfNewFbUser(fbId);
    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      User user;
      if (newFbUser) {
        // create new user.
        user = User.createFbUser(fbId, fbName, email, role);
        ofy.put(user);

        // create new UserMetadata.
        UserMetadataDao.instance.create(user, ofy);

        // increase the user count.
        CounterDao.addUser(ofy);

        // send a welcome email.
        EmailTaskQueueService.instance.pushWelcomeEmail(user);
      } else {
        user = UserDao.instance.getByFbId(fbId);
        if (user != null && hasChanges(user, fbName, email, role)) {
          user.setFbName(fbName);
          if (email != null) {
            user.setEmail(email);
          }
          user.setUserRole(role);
          ofy.put(user);
        }
      }
      ofy.getTxn().commit();
      return user;
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }

  @Override
  public Problem createProblem(Problem problem) {
    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      long id = CounterDao.getNextAvailableProblemId(ofy);
      problem.setId(id);
      ofy.put(problem);
      CounterDao.addProblem(ofy);
      ofy.getTxn().commit();
      return problem;
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }

  @Override
  public Problem saveProblem(Problem problem) {
    ObjectifyService.begin().put(problem);
    return problem;
  }

  @Override
  public void deleteProblem(long problemId) {
    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      ofy.delete(Problem.class, problemId);
      CounterDao.subtractProblem(ofy);
      ofy.getTxn().commit();
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }

  @Override
  public ProblemAttempt attemptProblem(long userId, long problemId) {
    User user = UserDao.instance.getById(userId);
    Problem problem = ProblemDao.instance.getById(problemId);

    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      ProblemAttempt attempt =
          new ProblemAttempt(user, problemId, user.getFbName(), problem.getTitle(), false);
      ofy.put(attempt);

      // increase the attempt counter of the problem.
      problem.addAttempter();
      ofy.put(problem);

      // increase the attempt counter of the user.
      UserMetadata userMetadata = UserMetadataDao.instance.getByUser(user, ofy);
      userMetadata.incrementAttempt();
      ofy.put(userMetadata);

      // increase the attempt counter of the user to the problem.
      ProblemUserMetadata problemUserMetadata =
          ProblemUserMetadataDao.instance.getByUserAndProblem(user, problemId, ofy);
      if (problemUserMetadata == null) {
        problemUserMetadata = new ProblemUserMetadata(user, problemId);
      }
      problemUserMetadata.incrementAttempt();
      ofy.put(problemUserMetadata);

      ofy.getTxn().commit();
      return attempt;
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }

  private int computeLevel(int solves, long problemCount) {
    return (int) ((solves * 12) / problemCount);
  }

  @Override
  public Solution solveProblem(long userId, long problemId, ProblemAttempt attempt) {
    User user = UserDao.instance.getById(userId);
    Problem problem = ProblemDao.instance.getById(problemId);
    String solutionId = Solution.createId(user.getId(), problem.getId());
    long problemCount = (int) CounterDao.getProblemsCount();

    Objectify ofy = ObjectifyService.beginTransaction();
    try {
      Solution solution = null;
      if (SolutionDao.instance.getById(user.getId(), solutionId) == null) {
        solution = new Solution(user, problem.getId(), user.getFbName(), problem.getTitle());
        SolutionDao.instance.save(solution);

        // increase the attempt counter of the problem.
        problem.addSolver();
        ofy.put(problem);

        // increase the attempt counter of the user.
        UserMetadata userMetadata = UserMetadataDao.instance.getByUser(user, ofy);
        userMetadata.incrementSolve();
        ofy.put(userMetadata);

        UserRankerDao.instance.increaseRank(userMetadata.getSolves(), ofy);

        int oldLevel = computeLevel(userMetadata.getSolves() - 1, problemCount);
        int newLevel = computeLevel(userMetadata.getSolves(), problemCount);
        if (oldLevel != newLevel) {
          EmailTaskQueueService.instance.pushLevelUpEmail(userId, oldLevel, newLevel);
          EmailHistoryDao.instance.save(userId);
        }

        attempt.setSuccessful(true);
        ofy.put(attempt);

        ofy.getTxn().commit();
      }
      return solution;
    } finally {
      if (ofy.getTxn().isActive()) {
        ofy.getTxn().rollback();
      }
    }
  }
}
