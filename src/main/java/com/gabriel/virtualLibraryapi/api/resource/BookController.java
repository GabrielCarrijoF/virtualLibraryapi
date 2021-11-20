package com.gabriel.virtualLibraryapi.api.resource;

import com.gabriel.virtualLibraryapi.api.dto.BookDTO;
import com.gabriel.virtualLibraryapi.api.exeption.ApiErros;
import com.gabriel.virtualLibraryapi.api.exeption.BusinessExecption;
import com.gabriel.virtualLibraryapi.model.entity.Book;
import com.gabriel.virtualLibraryapi.service.BookService;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")

public class BookController {

  private BookService service;
  private ModelMapper modelMapper;

  public BookController(BookService service, ModelMapper mapper) {
    this.service = service;
    this.modelMapper = mapper;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookDTO create(@RequestBody @Valid BookDTO dto) {

    Book entity = modelMapper.map(dto, Book.class);
    entity = service.save(entity);
    return modelMapper.map(entity, BookDTO.class);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErros handleValidationExepctions(MethodArgumentNotValidException ex) {

    BindingResult bindingResult = ex.getBindingResult();
    return new ApiErros(bindingResult);
  }

  @ExceptionHandler(BusinessExecption.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErros handleBusinessExecption (BusinessExecption erroMessage){
    return new ApiErros(erroMessage);
  }
}

