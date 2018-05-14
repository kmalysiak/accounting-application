package pl.coderstrust.e2e.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
public class User {

  private String username;
  private String password;

}
