package com.gabriel.virtualLibraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.model.repository.BookRepository;
import com.gabriel.virtualLibraryapi.service.impl.BookServiceImp;
import java.util.Optional;
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

  @Test
  @DisplayName("Deve obter livro por Id")
  public void getById() {
    Long id = 1L;

    Book book = createValidBook();
    book.setId(id);
    Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

    Optional<Book> foundBook = service.getById(id);

    assertThat(foundBook.isPresent()).isTrue();
    assertThat(foundBook.get().getId()).isEqualTo(id);
    assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
    assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
  }

  @Test
  @DisplayName("Deve retornar vazio quando buscar um livro por Id")
  public void bookNotFoundById() {
    Long id = 1L;

    Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

    Optional<Book> book= service.getById(id);

    assertThat(book.isPresent()).isFalse();
  }

  @Test
  @DisplayName("Deve deletar um livro")
  public void bookDeleteTeste(){

    Book book = Book.builder().id(1L).build();

   org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

    Mockito.verify(repository, Mockito.times(1)).delete(book);

  }

  @Test
  @DisplayName("Não ocorrer erro ao tentar deletar um livro")
  public void deleteInvalidBookTest(){
    Book book = new Book();

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class,() -> service.delete(book));

    Mockito.verify(repository, Mockito.never()).delete(book);
  }

  @Test
  @DisplayName("Deve atualizar um livro")
  public void bookUpdateTeste(){
    long id = 1L;
    Book updatingBook= Book.builder().id(id).build();

    Book updatedBook = createValidBook();
    updatedBook.setId(id);

    Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

    Book book = service.update(updatingBook);

    assertThat(book.getId()).isEqualTo(updatedBook.getId());
    assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
    assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
  }

  @Test
  @DisplayName("Não ocorrer erro ao atualizar um livro")
  public void updateInvalidBookTest(){
    Book book = new Book();

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class,() -> service.update(book));

    Mockito.verify(repository, Mockito.never()).save(book);
  }

}
