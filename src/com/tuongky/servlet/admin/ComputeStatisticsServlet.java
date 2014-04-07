package com.tuongky.servlet.admin;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.StatisticsDao;

/**
 * Created by sngo on 4/6/14.
 */
@SuppressWarnings("serial")
public class ComputeStatisticsServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(ComputeStatisticsServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      StatisticsDao.instance.put();
      resp.getWriter().println("Ok");
    } catch (Exception e) {
      log.severe("Fail to set statistics: " + e.getStackTrace());
      resp.getWriter().println("Fail " + e.getMessage());
    }
  }
}