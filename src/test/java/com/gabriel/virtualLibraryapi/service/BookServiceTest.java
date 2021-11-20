package com.gabriel.virtualLibraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.model.repository.BookRepository;
import com.gabriel.virtualLibraryapi.service.impl.BookServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

  BookService service;

  @MockBean
  BookRepository repository;

  @BeforeEach
  public void setUp() {
    this.service = new BookServiceImp(repository);
  }

  @Test
  @DisplayName("Must Save a Book")
  public void saveBookTest() {
    Book book = Book.builder().isbn("001").author("Author").title("Devezinho do Teste").build();

    Mockito.when(repository.save(book))
        .thenReturn(Book.builder()
            .id(11L)
            .isbn("001")
            .author("Author")
            .title("Devezinho do Teste")
            .build()
        );

    Book savedBook = service.save(book);

    assertThat(savedBook.getId()).isNotNull();
    assertThat(savedBook.getIsbn()).isEqualTo("001");
    assertThat(savedBook.getTitle()).isEqualTo("Devezinho do Teste");
    assertThat(savedBook.getAuthor()).isEqualTo("Author");

  }
}
