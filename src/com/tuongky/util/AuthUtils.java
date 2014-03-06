package com.tuongky.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.model.UserRole;
import com.tuongky.model.datastore.Session;
import com.tuongky.servlet.Constants;

public final class AuthUtils {

  private AuthUtils() {}

  private static boolean hasRole(UserRole userRole, HttpServletRequest req) {
    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
    if (session == null) {
      return userRole == UserRole.ANONYMOUS;
    }
    return userRole.getValue() <= session.getUserRole().getValue();
  }

  public static boolean isAdmin(HttpServletRequest req) {
    return hasRole(UserRole.ADMIN, req);
  }

  public static boolean isModerator(HttpServletRequest req) {
    return hasRole(UserRole.MODERATOR, req);
  }

  public static boolean isUser(HttpServletRequest req) {
    return hasRole(UserRole.USER, req);
  }

  public static boolean allowIf(
      UserRole userRole, HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Session session = (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
    if (session == null) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }
    if (!hasRole(userRole, req)) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
    return true;
  }

  public static boolean allowAdminOnly(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    return allowIf(UserRole.ADMIN, req, resp);
  }

  public static Session getSession(HttpServletRequest req) {
    return (Session) req.getAttribute(Constants.SESSION_ATTRIBUTE);
  }
}
