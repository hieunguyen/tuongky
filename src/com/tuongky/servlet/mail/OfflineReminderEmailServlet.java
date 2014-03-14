package com.tuongky.servlet.mail;

import com.tuongky.backend.EmailHistoryDao;
import com.tuongky.backend.UserDao;
import com.tuongky.backend.UserMetadataDao;
import com.tuongky.model.datastore.EmailHistory;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/** Offline job
 *
 * Created by sngo on 3/9/14.
 */
public class OfflineReminderEmailServlet extends HttpServlet {

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    List<UserMetadata> userSet = UserMetadataDao.instance.getAllUsers();

    for (UserMetadata user : userSet) {
      EmailHistoryDao.instance.spamRemind(user.getId());
      EmailHistoryDao.instance.notifyLevelDown(user.getId(), UserMetadataDao.computeLevel(user.getSolves()));
    }

    resp.getWriter().println(JsonUtils.toJson("result", "Ok"));
  }
}
