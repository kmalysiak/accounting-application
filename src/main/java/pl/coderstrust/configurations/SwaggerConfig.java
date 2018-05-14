package pl.coderstrust.configurations;

import com.google.common.base.Predicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(Predicates.not(PathSelectors.regex("/error.*")))
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Invoices API")
        .description("The panel below displays documentation all endpoints, parameters and error "
            + "messages available to the Invoices API. Here you can check the operation of such "
            + "operations as calculating the taxes, adding invoices or editing them.")
        .version("1.2.0")
        .contact(new Contact("Green Team", "www.green-team.com",
            "green.team.coderstrust@gmail.com"))
        .build();
  }

}