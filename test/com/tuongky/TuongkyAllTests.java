package com.tuongky;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.tuongky.backend.CounterDaoTest;
import com.tuongky.backend.ProblemAttemptDaoTest;
import com.tuongky.backend.SolutionDaoTest;
import com.tuongky.backend.UserDaoTest;

@RunWith(Suite.class)
@SuiteClasses({
  CounterDaoTest.class,
  ProblemAttemptDaoTest.class,
  SolutionDaoTest.class,
  UserDaoTest.class,
})
public class TuongkyAllTests {}
