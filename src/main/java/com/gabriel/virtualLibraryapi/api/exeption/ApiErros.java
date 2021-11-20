package com.gabriel.virtualLibraryapi.api.exeption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.BindingResult;

public class ApiErros {

  private List<String> erros;

  public ApiErros(BindingResult bindingResult) {
    this.erros = new ArrayList<>();
    bindingResult.getAllErrors().forEach(erros -> this.erros.add(erros.getDefaultMessage()));
  }

  public ApiErros(BusinessExecption erroMessage) {
    this.erros = Arrays.asList(erroMessage.getMessage());
  }

  public List<String> getErros(){
    return erros;
  }
}
