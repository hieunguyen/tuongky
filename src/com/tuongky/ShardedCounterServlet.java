package com.tuongky;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.service.ShardedCounter;

/**
 * A servlet for incrementing a configurable sharded counter.
 */
@SuppressWarnings("serial")
public class ShardedCounterServlet extends HttpServlet {
  @Override
  public final void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String counterName = req.getParameter("name");
    String action = req.getParameter("action");
    String shards = req.getParameter("shards");

    ShardedCounter counter = ShardedCounter.getOrCreateCounter(counterName, 5);

    if ("increment".equals(action)) {
      counter.increment();
      resp.getWriter().println("Counter incremented.");
    } else if ("increase_shards".equals(action)) {
      int inc = Integer.valueOf(shards);
      counter.addShards(inc);
      resp.getWriter().println("Shard count increased by " + inc + ".");
    } else {
      resp.getWriter().println("getCount() -> " + counter.getCount());
    }
  }
}
