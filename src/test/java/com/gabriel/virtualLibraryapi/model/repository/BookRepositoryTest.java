package com.gabriel.virtualLibraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gabriel.virtualLibraryapi.model.entity.Book;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

  @Autowired
  TestEntityManager entityManager;

  @Autowired
  BookRepository repository;

  private Book createNewBook(String isbn) {
    return Book.builder().isbn("001").author("Author").title("Devezinho do Teste").build();
  }


  @Test
  @DisplayName("Must return true when there is a book in the base with isbn informed")
  public void returnTrueWhenIsbnExists() {
    String isbn = "001";
    Book book = Book.builder().isbn("001").author("Author").title("Devezinho do Teste").build();
    entityManager.persist(book);

    boolean exists = repository.existsByIsbn(isbn);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Must return false when there is a book in the base with isbn informed")
  public void returnfalseWhenIsbnDoesntExists() {
    String isbn = "001";

    boolean exists = repository.existsByIsbn(isbn);
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Deve Obter um livro por id")
  public void findByIdTest() {
    Book book = createNewBook("001");
    entityManager.persist(book);

    Optional<Book> foundBook = repository.findById(book.getId());

    assertThat(foundBook.isPresent()).isTrue();
  }

  @Test
  @DisplayName("Deve salvar um livro")
  public void savedBookTeste() {
    Book book = createNewBook("001");

    Book savedBook = repository.save(book);

    assertThat(savedBook.getId()).isNotNull();
  }

  @Test
  @DisplayName("Deve deletar um livro")
  public void deleteBookTest() {
    Book book = createNewBook("001");
    entityManager.persist(book);

    Book foundBook = entityManager.find(Book.class, book.getId());
    repository.delete(foundBook);

    Book deletedBook = entityManager.find(Book.class, book.getId());
    assertThat(deletedBook).isNull();
  }
}
