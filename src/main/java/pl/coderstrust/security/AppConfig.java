package pl.coderstrust.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Profile("simpleOAuth")
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

  @Autowired
  RequestInterceptor requestInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestInterceptor);
  }
}



