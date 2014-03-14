package com.tuongky.servlet.admin;

import com.tuongky.backend.SolutionDao;
import com.tuongky.backend.UserMetadataDao;
import com.tuongky.model.datastore.UserMetadata;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by sngo on 3/13/14.
 */
public class RecomputeUserSolvesCounterServlet extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    List<UserMetadata> userSet = UserMetadataDao.instance.getAllUsers();

    for (UserMetadata user : userSet) {
      int solves = SolutionDao.instance.getSolves(user.getId());
      UserMetadataDao.instance.setSolves(user.getId(), solves);
    }
  }
}
