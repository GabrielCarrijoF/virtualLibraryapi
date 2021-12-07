package com.gabriel.virtualLibraryapi.api.resource;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.virtualLibraryapi.api.dto.BookDTO;
import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.service.BookService;
import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

  static String BOOK_API = "/api/books";

  @Autowired
  MockMvc mvc;

  @MockBean
  BookService service;

  private BookDTO createNewBook() {
    return BookDTO.builder().author("Author").title("Devezinho do Teste").isbn("001").build();
  }

  @Test
  @DisplayName("Must create a book successfully")
  public void createBookTest() throws Exception {

    BookDTO bookDTO = createNewBook();

    Book savedBook = Book.builder().id(10L).author("Author").title("Devezinho do Teste").isbn("001")
        .build();
    BDDMockito.given(service.save(Mockito.any(Book.class)))
        .willReturn(savedBook);

    String json = new ObjectMapper().writeValueAsString(bookDTO);

    var request = MockMvcRequestBuilders
        .post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    mvc
        .perform(request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").isNotEmpty())
        .andExpect(jsonPath("title").value(bookDTO.getTitle()))
        .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
        .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));

  }

  @Test
  @DisplayName("Must Post validation error when there is not enough data to create the book")
  public void createInvalidBookTest() throws Exception {

    String json = new ObjectMapper().writeValueAsString(new BookDTO());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("erros", hasSize(3)));

  }

  @Test
  @DisplayName("It should throw an error when trying to register a book with isbn already used by another")
  public void createBookWithDuplicateIsbn() throws Exception {

    BookDTO dto = createNewBook();

    String json = new ObjectMapper().writeValueAsString(dto);

    String messageErro = "Isbn já cadastrada";

    BDDMockito.given(service.save(Mockito.any(Book.class)))
        .willThrow(new BusinessExecption(messageErro));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("erros", hasSize(1)))
        .andExpect(jsonPath("erros[0]").value(messageErro));
  }

  @Test
  @DisplayName(" Must get information from a book")
  public void getBookDetailsTest() throws Exception {
    //cenario(given)
    Long id = 11L;

    Book book = Book.builder()
        .id(id)
        .title(createNewBook().getTitle())
        .author(createNewBook().getAuthor())
        .isbn(createNewBook().getIsbn()).build();

    BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

    //execução (when)
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .get(BOOK_API.concat("/" + id))
        .accept(MediaType.APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id))
        .andExpect(jsonPath("title").value(createNewBook().getTitle()))
        .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
        .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

  }

  @Test
  @DisplayName("Should return a resource not found when the book looking for does not exist")
  public void bookNotFoundTest() throws Exception {

    BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .get(BOOK_API.concat("/" + 1L))
        .accept(MediaType.APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Must delete a book")
  public void deleteBookTeste() throws Exception {

    BDDMockito.given(service.getById(Mockito.anyLong()))
        .willReturn(Optional.of(Book.builder().id(11L).build()));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .delete(BOOK_API.concat("/" + 11L));

    mvc.perform(request)
        .andExpect(status().isNoContent());

  }

  @Test
  @DisplayName("Must delete a book")
  public void notFondBookTeste() throws Exception {

    BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .delete(BOOK_API.concat("/" + 11L));

    mvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve atualizar um livro")
  public void updateBookTest() throws Exception {

    Long id = 11L;
    String json = new ObjectMapper().writeValueAsString(createNewBook());

    Book updatingBook = Book.builder().id(11L)
        .title("some Devezinho")
        .author("some Author")
        .isbn("002")
        .build();

    BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));

    Book updateBook = Book.builder().id(id).author("Author").title("Devezinho do Teste").isbn("001")
        .build();

    BDDMockito.given(service.update(updatingBook)).willReturn(updateBook);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .put(BOOK_API.concat("/" + 11L))
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id))
        .andExpect(jsonPath("title").value(createNewBook().getTitle()))
        .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
        .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

  }

  @Test
  @DisplayName("Deve retornar 404 quando atualizar um livro")
  public void updateInexistenBookTest() throws Exception {

    String json = new ObjectMapper().writeValueAsString(createNewBook());

    BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .put(BOOK_API.concat("/" + 11L))
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isNotFound());

  }

  @Test
  @DisplayName("Deve filtrar um livro")
  public void findBookTest() throws Exception {
    Long id = 1l;

    Book book = Book.builder().id(id)
        .author(createNewBook().getAuthor())
        .title(createNewBook().getTitle())
        .isbn(createNewBook().getIsbn())
        .build();

    BDDMockito.given(service.find
        (Mockito.any(Book.class),
            Mockito.any(Pageable.class)))
        .willReturn(new PageImpl<Book>(Arrays.asList(book),
            PageRequest.of(0, 100), 1));

    String queryString = String.format("?title=%sauthor=%s&spage=0&size=100",
        book.getTitle(), book.getAuthor());

  MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
      .get(BOOK_API.concat(queryString))
      .accept(MediaType.APPLICATION_JSON);

  mvc
      .perform(requestBuilder)
      .andExpect(status().isOk())
      .andExpect(jsonPath("content", Matchers.hasSize(1)))
      .andExpect(jsonPath("totalElements").value(1))
      .andExpect(jsonPath("pagaeble.pageSize").value(100))
      .andExpect(jsonPath("pageable.pageNumber").value(0));
  }
}
