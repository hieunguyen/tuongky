package com.tuongky.util;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public final class JsonUtils {

  private JsonUtils() {}

  public static String toJson(Object... objs) {
    Preconditions.checkArgument(objs.length % 2 == 0, "Number of arguments must be even.");
    Map<String, Object> map = Maps.newHashMap();
    for (int i = 0; i < objs.length; i += 2) {
      map.put(objs[i].toString(), objs[i + 1]);
    }
    return new Gson().toJson(map);
  }
}
