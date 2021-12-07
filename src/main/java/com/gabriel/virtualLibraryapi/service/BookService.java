package com.gabriel.virtualLibraryapi.service;

import com.gabriel.virtualLibraryapi.model.entity.Book;
import java.awt.print.Pageable;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BookService {
  Book save(Book any);

  Optional<Book> getById(Long id);

  void delete(Book book);

  Book update(Book book);

  Page<Book> find(Book any, Pageable any1);
}
