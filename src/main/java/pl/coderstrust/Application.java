package pl.coderstrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  //TODO Modify Swagger to show field for token header in SimpleOAuth Profile

  // To run app without need of sending tokens in request, run basicSecurity
  // profile both Spring profile and Maven profile

  // To run angular frontEnd App run with no security - leave active profile empty in properties
  // and maven profiles.


}
