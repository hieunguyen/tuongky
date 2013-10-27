package com.tuongky;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tuongky.backend.BookDao;
import com.tuongky.model.datastore.Book;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class BookListServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String username = req.getParameter("username");
    String lastCreatedAtStr = req.getParameter("last_created_at");
    long lastCreatedAt = 0;
    if (lastCreatedAtStr != null) {
      lastCreatedAt = Long.parseLong(lastCreatedAtStr);
    }
    if (username == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username cannot be null.");
      return;
    }
    BookDao bookDao = new BookDao();
    List<Book> books = bookDao.getByUsername(username, lastCreatedAt);
    resp.setContentType(Constants.CT_JSON_UTF8);
    resp.getWriter().println(JsonUtils.toJson("books", books));
  }
}
