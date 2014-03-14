package com.tuongky.servlet.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tuongky.backend.CounterDao;
import com.tuongky.backend.UserDao;
import com.tuongky.backend.UserMetadataDao;
import com.tuongky.model.UserData;
import com.tuongky.model.datastore.User;
import com.tuongky.model.datastore.UserMetadata;
import com.tuongky.util.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
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

  private static final String PAGE_NUM_FIELD = "page_num";
  private static final String PAGE_SIZE_FIELD = "page_size";

  private static final String ROOT_KEY = "usersRank";
  private static final String TOTAL_RESULT = "total";
  private static final String PROBLEM_COUNT = "problemCount";

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
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
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

      Map<Long, User> userMap = UserDao.instance.batchGetById(userIds);

      long totalResults = CounterDao.getUsersCount();
      long problemCount = CounterDao.getProblemsCount();

      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, prepareResults(list, userMap),
              TOTAL_RESULT, totalResults, PROBLEM_COUNT, problemCount));

    } catch (NumberFormatException e){
      resp.getWriter().println(JsonUtils.toJson(ROOT_KEY, "NumberFormatException"));
    }
  }
}
