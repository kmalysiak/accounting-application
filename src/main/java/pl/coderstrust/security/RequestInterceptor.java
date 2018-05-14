package pl.coderstrust.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Profile("simpleOAuth")
public class RequestInterceptor extends HandlerInterceptorAdapter {

  private static final String DEFAULT_URL_TO_VALIDATE_TOKEN =
      "http://localhost:8080/tokens/validate/";
  private static final String pattern = "(\\/users.*|\\/tokens.*|\\/swagger.*)";

  private final Logger logger = LoggerFactory.getLogger("Security");
  private static RestTemplate restTemplate = new RestTemplate();


  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response, Object object) throws Exception {

    if (request.getPathInfo().matches(pattern)) {
      return true;
    }

    String requestToken = request.getHeader("Token");
    if (requestToken == null || requestToken.isEmpty()) {
      logger.error("No request token.");
      response.getWriter().write("Incorrect Token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    if (isTokenCorrect(DEFAULT_URL_TO_VALIDATE_TOKEN, requestToken)) {
      return true;
    } else {
      logger.error("Request token is incorrect.");
      response.getWriter().write("Incorrect Token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
  }

  private static boolean isTokenCorrect(String defaultUrl, String token) {
    ResponseEntity<String> tokenValidation
        = restTemplate.getForEntity(defaultUrl + token, String.class);
    return Boolean.valueOf(tokenValidation.getBody());
  }

  private static boolean isOkRequest(String pattern, String stringToCheck) {
    return pattern.matches(stringToCheck);
  }
}
