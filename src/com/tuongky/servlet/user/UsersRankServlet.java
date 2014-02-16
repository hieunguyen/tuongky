package com.tuongky.servlet.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tuongky.backend.UserDao;
import com.tuongky.backend.UserMetadataDao;
import com.tuongky.model.UserData;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.util.JsonUtils;

import javax.servlet.http.HttpServlet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sngo on 2/13/14.
 *
 * Find a list of users sort by #problemsSolved
 */
@SuppressWarnings("serial")
public class UsersRankServlet extends HttpServlet {

  private static final String PAGE_NUM_FIELD = "pageNum";
  private static final String PAGE_SIZE_FIELD = "pageSize";

  private static final String ROOT_KEY = "usersRank";

  private static int PAGE_SIZE_DEFAULT = 10;

  private Set<Long> extractUserId(List<UserMetadata> userMetadata){
    Set<Long> ids = Sets.newHashSet();
    for (UserMetadata data : userMetadata){
      ids.add(data.getId());
    }

    return ids;
  }

  private List<UserData> prepareResults(List<UserMetadata> users, Map<Long, User> userMap){
    List<UserData> ret = Lists.newArrayList();

    for (UserMetadata userMetadata : users){
      User user = userMap.get(userMetadata.getId());
      ret.add(new UserData(user, userMetadata));
    }

    return ret;
  }
  // order can be any field in UserMetadata.
  @Override
  public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    String pageNum = req.getParameter(PAGE_NUM_FIELD);
    String pageSize = req.getParameter(PAGE_SIZE_FIELD);

    try{
      int page = Integer.parseInt(pageNum);
      int size = PAGE_SIZE_DEFAULT;

      if (pageSize != null && !pageSize.isEmpty()) {
        try {
          size = Integer.parseInt(pageSize);
        }
        catch (NumberFormatException e){
          resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
          return;
        }
      }

      int startIndex = page * size;

      List<UserMetadata> list = UserMetadataDao.instance.search(startIndex, size);
      Set<Long> userIds = extractUserId(list);

      Map<Long, User> userMap = UserDao.instance.batchGetBuyId(userIds);

      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, prepareResults(list, userMap)));

    } catch (NumberFormatException e){
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
    }
  }

}
