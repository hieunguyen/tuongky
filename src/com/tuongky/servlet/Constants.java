package com.tuongky.servlet;

import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableMap;
import com.tuongky.model.UserRole;

public final class Constants {

  public static final String CT_JSON_UTF8 = "application/json; charset=UTF-8";
  public static final String CT_HTML_UTF8 = "text/html; charset=UTF-8";

  public static final String SESSION_COOKIE = "sid";
  public static final String SESSION_ATTRIBUTE = "session_attribute";

  public static final Map<String, UserRole> USER_ROLE_MAP = ImmutableMap.of("501924587", UserRole.ADMIN, "1030390940", UserRole.ADMIN);
  private Constants() {}
}
