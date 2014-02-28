package com.tuongky.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuongky.backend.SessionDao;
import com.tuongky.backend.UserDao;
import com.tuongky.model.datastore.Session;
import com.tuongky.model.datastore.User;

@SuppressWarnings("serial")
public class FacebookSigninServlet extends HttpServlet {

  private static class FbAuth {

    private String fbId = null;
    private String fbName = null;

    FbAuth(String accessToken) {
      String json = makeGraphApiRequest(accessToken);
      if (json == null) {
        return;
      }
      Gson gson = new Gson();
      Type fooType = new TypeToken<HashMap<String, Object>>() {}.getType();
      HashMap<String, Object> map = gson.fromJson(json, fooType);
      fbId = (String) map.get("id");
      fbName = (String) map.get("name");
    }

    private String makeGraphApiRequest(String accessToken) {
      String requestUrl =
          "https://graph.facebook.com/me?fields=id,name&access_token=" + accessToken;
      try {
        URL url = new URL(requestUrl);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
          StringBuilder sb = new StringBuilder();
          while (true) {
            String line = reader.readLine();
            if (line == null) break;
            sb.append(line);
          }
          return sb.toString();
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    public boolean isAuthenticated() {
      return fbId != null;
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String accessToken = req.getParameter("access_token");
    if (accessToken == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "accessToken cannot be null.");
      return;
    }

    FbAuth fbAuth = new FbAuth(accessToken);
    if (!fbAuth.isAuthenticated()) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          String.format("AccessToken '%s' is invalid.", accessToken));
      return;
    }

    UserDao userDao = new UserDao();
    User user = userDao.getByFbId(fbAuth.fbId);
    if (user == null) { // New user -> create user.
      user = userDao.save(fbAuth.fbId, fbAuth.fbName);
    } else if (!user.getFbName().equals(fbAuth.fbName)) {
      user.setFbName(fbAuth.fbName);
      user = userDao.save(user);
    }
    Session session = new SessionDao().save(user.getId(), user.getUserRole());
    Map<String, Object> data = new HashMap<>();
    data.put(Constants.SESSION_COOKIE, session.getId());
    data.put("fbId", user.getFbId());
    data.put("fbName", user.getFbName());
    data.put("roleId", user.getRoleIndex());
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(new Gson().toJson(data));
  }
}
