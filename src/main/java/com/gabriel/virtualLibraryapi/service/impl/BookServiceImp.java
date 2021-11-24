package com.gabriel.virtualLibraryapi.service.impl;

import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.model.repository.BookRepository;
import com.gabriel.virtualLibraryapi.service.BookService;
import java.util.Optional;
import org.springframework.stereotype.Service;
@Service
public class BookServiceImp implements BookService {

  private BookRepository repository;

  public BookServiceImp(final BookRepository repository) {
    this.repository = repository;
  }


  @Override
  public Book save(final Book book) {
    if (repository.existsByIsbn(book.getIsbn())){
      throw new BusinessExecption("Isbn já cadastrada");
    }
    return repository.save(book);
  }

  @Override
  public Optional<Book> getById(final Long id) {
    return Optional.empty();
  }

  @Override
  public void delete(final Book book) {

  }
}
