package com.tuongky.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.tuongky.backend.SessionDao;
import com.tuongky.model.datastore.Session;

public class AuthFilter implements Filter {

  @Override
  public void destroy() {}

  public String getSessionId(HttpServletRequest req) {
    Cookie[] cookies = req.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(Constants.SESSION_COOKIE)) {
        return cookie.getValue();
      }
    }
    return null;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    String sid = getSessionId((HttpServletRequest) servletRequest);
    if (sid != null) {
      SessionDao sessionDao = new SessionDao();
      Session session = sessionDao.getById(sid);
      if (session != null) {
        servletRequest.setAttribute(Constants.SESSION_ATTRIBUTE, session);
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}
