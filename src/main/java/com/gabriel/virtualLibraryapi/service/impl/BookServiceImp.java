package com.gabriel.virtualLibraryapi.service.impl;

import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.model.repository.BookRepository;
import com.gabriel.virtualLibraryapi.service.BookService;
import org.springframework.stereotype.Service;
@Service
public class BookServiceImp implements BookService {

  private BookRepository repository;

  public BookServiceImp(final BookRepository repository) {
    this.repository = repository;
  }


  @Override
  public Book save(final Book book) {
    return repository.save(book);
  }
}
