package pl.coderstrust.model;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class User implements WithValidation {

  private String username;
  private String password;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public User() {
  }

  @ApiModelProperty(example = "Tomek")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @ApiModelProperty(example = "haslo")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

  @Override
  public List<String> validate() {
    List<String> errors = new ArrayList<>();
    if (checkInputString(this.username)) {
      errors.add(Messages.NO_USERNAME);
    }
    if (checkInputString(this.password)) {
      errors.add(Messages.NO_PASSWORD);
    }
    return errors;
  }
}
