package com.gabriel.virtualLibraryapi.service;

import com.gabriel.virtualLibraryapi.model.entity.Book;
import java.util.Optional;

public interface BookService {
  Book save(Book any);

  Optional<Book> getById(Long id);
}
