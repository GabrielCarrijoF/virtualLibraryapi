package com.gabriel.virtualLibraryapi.api.resource;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.virtualLibraryapi.api.dto.BookDTO;
import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
}
