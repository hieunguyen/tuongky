package com.tuongky.servlet.user;

import com.tuongky.backend.UserRankerDao;

import javax.servlet.http.HttpServlet;

/**
 * Created by sngo on 2/16/14.
 */
@SuppressWarnings("serial")
public class RankerServlet extends HttpServlet {
  @Override
  protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
          throws javax.servlet.ServletException, java.io.IOException {
    UserRankerDao.instance.recomputeRanking();
  }

}
