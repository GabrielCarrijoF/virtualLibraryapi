package com.gabriel.virtualLibraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.model.repository.BookRepository;
import com.gabriel.virtualLibraryapi.service.impl.BookServiceImp;
import org.assertj.core.api.Assertions;
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

  private Book createValidBook() {
    return Book.builder().isbn("001").author("Author").title("Devezinho do Teste").build();
  }

  @Test
  @DisplayName("Must Save a Book")
  public void saveBookTest() {
    Book book = createValidBook();
    Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

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

  @Test
  @DisplayName("Deve laçar um erro de negocio ao tentar salvar um livro com isbn duplicado")
  public void shouldNotSaveABookWithDuplicateISBN() {
    Book book = createValidBook();
    Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

    Throwable exception = Assertions.catchThrowable(() -> service.save(book));

    assertThat(exception)
        .isInstanceOf(BusinessExecption.class)
        .hasMessage("Isbn já cadastrada");

    Mockito.verify(repository, Mockito.never()).save(book);
  }
}
