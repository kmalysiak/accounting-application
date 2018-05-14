package pl.coderstrust.service.tokens;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TokenServiceTest {

  private TokenService tokenService = new TokenService();

  @Test
  public void shouldGenerateToken() {
    //given
    final String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    //when
    String tokenNumber = tokenService.generateToken();
    //then
    assertTrue(tokenNumber.matches(uuidPattern));
    assertTrue(tokenService.validateToken(tokenNumber));
    assertTrue(tokenService.tokenExist(tokenNumber));
  }

  @Test
  public void shouldValidateToken() {
    //given
    String tokenNumber = tokenService.generateToken();
    String notValidToken = "1234-bla-bla";
    //when
    assertTrue(tokenService.validateToken(tokenNumber));
    assertFalse(tokenService.validateToken(notValidToken));
  }

  @Test
  public void tokenExist() {
    //given
    String tokenNumber = tokenService.generateToken();
    String notValidToken = "1234-bla-bla";
    //when
    assertTrue(tokenService.tokenExist(tokenNumber));
    assertFalse(tokenService.tokenExist(notValidToken));
  }
}