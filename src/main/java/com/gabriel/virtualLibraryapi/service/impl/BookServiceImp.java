package com.gabriel.virtualLibraryapi.service.impl;

import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.model.repository.BookRepository;
import java.awt.print.Pageable;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
@Service
public class BookServiceImp implements com.gabriel.virtualLibraryapi.service.BookService {

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
  public Optional<Book> getById( Long id) {
    return this.repository.findById(id);
  }

  @Override
  public void delete(final Book book) {
    if (book == null || book.getId() ==null){
      throw new IllegalArgumentException("Book id cant be null");
    }
    this.repository.delete(book);
  }

  @Override
  public Book update(final Book book) {
    if (book == null || book.getId() ==null){
      throw new IllegalArgumentException("Book id cant be null");
    }
    return this.repository.save(book);
  }

  @Override
  public Page<Book> find(final Book any, final Pageable any1) {
    return null;
  }
}
