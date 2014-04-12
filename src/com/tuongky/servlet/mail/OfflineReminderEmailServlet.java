package com.tuongky.servlet.mail;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.EmailHistoryDao;
import com.tuongky.backend.UserMetadataDao;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.util.JsonUtils;

/** Offline job
 *
 * Created by sngo on 3/9/14.
 */
@SuppressWarnings("serial")
public class OfflineReminderEmailServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<UserMetadata> userSet = UserMetadataDao.instance.getAllUserMetadatas();

    for (UserMetadata user : userSet) {
//      EmailHistoryDao.instance.spamRemind(user.getId());
      EmailHistoryDao.instance.notifyLevelDown(user.getId(), UserMetadataDao.computeLevel(user.getSolves()));
    }

    resp.getWriter().println(JsonUtils.toJson("result", "Ok"));
  }
}
