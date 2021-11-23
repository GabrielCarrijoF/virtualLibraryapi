package com.gabriel.virtualLibraryapi.model.repository;

import com.gabriel.virtualLibraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {


  boolean existsByIsbn(String isbn);

}
