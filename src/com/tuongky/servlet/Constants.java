package com.tuongky.servlet;

import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableList;

public final class Constants {

  public static final String CT_JSON = "application/json";
  public static final String CT_JSON_UTF8 = "application/json; charset=UTF-8";

  public static final String SESSION_COOKIE = "sid";
  public static final String SESSION_ATTRIBUTE = "session_attribute";

  public static final List<String> ADMIN_FB_IDS = ImmutableList.of("501924587");

  private Constants() {}
}
