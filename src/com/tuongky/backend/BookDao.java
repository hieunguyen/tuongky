package com.tuongky.backend;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.Book;

public class BookDao extends DAOBase {

  static {
    ObjectifyService.register(Book.class);
  }

  public Book save(String username, String name) {
    Book book = new Book(username, name);
    ObjectifyService.begin().put(book);
    return book;
  }

  public Book getById(long id) {
    return ObjectifyService.begin().find(Book.class, id);
  }

  public List<Book> getByUsername(String username, long lastCreatedAt) {
    return Lists.newArrayList(
        ObjectifyService.begin()
            .query(Book.class)
            .filter("username", username)
            .filter("createdAt >", lastCreatedAt)
            .fetch()
            .iterator());
  }

  public void delete(long id) {
    ObjectifyService.begin().delete(Book.class, id);
  }
}
