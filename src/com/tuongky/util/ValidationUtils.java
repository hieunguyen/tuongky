package com.tuongky.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ValidationUtils {

  private ValidationUtils() {}

  public static String mustBeSet(HttpServletRequest req, HttpServletResponse resp, String field)
      throws IOException {
    String value = req.getParameter(field);
    if (value == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, field + " cannot be null.");
      return null;
    }
    return value;
  }

  public static Integer mustBeInt(HttpServletRequest req, HttpServletResponse resp, String field)
      throws IOException {
    String value = mustBeSet(req, resp, field);
    if (value == null) {
      return null;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      resp.sendError(
          HttpServletResponse.SC_BAD_REQUEST, field + " must be an int number. Got: " + value);
      return null;
    }
  }

  public static Long mustBeLong(HttpServletRequest req, HttpServletResponse resp, String field)
      throws IOException {
    String value = mustBeSet(req, resp, field);
    if (value == null) {
      return null;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      resp.sendError(
          HttpServletResponse.SC_BAD_REQUEST, field + " must be a long number. Got: " + value);
      return null;
    }
  }

  public static Integer mayBeInt(HttpServletRequest req, HttpServletResponse resp,
      String field, int defaultValue) throws IOException {
    String value = req.getParameter(field);
    if (value == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      resp.sendError(
          HttpServletResponse.SC_BAD_REQUEST, field + " must be an int number. Got: " + value);
      return null;
    }
  }

  public static Long mayBeLong(HttpServletRequest req, HttpServletResponse resp,
      String field, long defaultValue) throws IOException {
    String value = req.getParameter(field);
    if (value == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      resp.sendError(
          HttpServletResponse.SC_BAD_REQUEST, field + " must be a long number. Got: " + value);
      return null;
    }
  }
}
