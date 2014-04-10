package com.tuongky.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.tuongky.model.datastore.Solution;
import com.tuongky.model.response.SolutionResponse;

public final class SolutionUtils {

  private SolutionUtils() {}

  public static List<SolutionResponse> toSolutionResponses(List<Solution> solutions) {
    List<SolutionResponse> solutionResponses = Lists.newArrayList();
    for (Solution solution : solutions) {
      solutionResponses.add(SolutionResponse.fromSolution(solution));
    }
    return solutionResponses;
  }
}
