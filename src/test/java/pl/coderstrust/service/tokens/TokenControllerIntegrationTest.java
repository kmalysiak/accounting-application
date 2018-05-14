package pl.coderstrust.service.tokens;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.model.User;
import pl.coderstrust.service.users.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser
public class TokenControllerIntegrationTest {

  private static final String GENERATE_TOKEN_PATH = "/tokens/generate";
  private static final String VALIDATE_TOKEN_PATH = "/tokens/validate";
  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private final String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

  private User user = new User("username", "userPassword");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private UserService userService;

  @Test
  public void shouldGenerateCorrectToken() throws Exception {
    //given
    Mockito.when(userService.usernameExist(user.getUsername())).thenReturn(true);
    Mockito.when(userService.validateUser(user.getUsername(), user.getPassword())).thenReturn(true);
    //when
    String generatedToken = this.mockMvc.perform(
        post(GENERATE_TOKEN_PATH).content(mapper.writeValueAsString(user))
            .contentType(JSON_CONTENT_TYPE)).andExpect(status().isOk()).andReturn().getResponse()
        .getContentAsString();
    //then
    String isTokenCorrect = this.mockMvc.perform(
        get(VALIDATE_TOKEN_PATH + "/" + generatedToken).content(generatedToken)
            .contentType(JSON_CONTENT_TYPE)).andExpect(status().isOk()).andReturn().getResponse()
        .getContentAsString();

    assertTrue(generatedToken.matches(uuidPattern));
    assertTrue(Boolean.valueOf(isTokenCorrect));
  }

  @Test
  public void shouldValidateCorrectToken() throws Exception {
    //given
    Mockito.when(userService.usernameExist(user.getUsername())).thenReturn(true);
    Mockito.when(userService.validateUser(user.getUsername(), user.getPassword())).thenReturn(true);
    String generatedToken = this.mockMvc.perform(
        post(GENERATE_TOKEN_PATH).content(mapper.writeValueAsString(user))
            .contentType(JSON_CONTENT_TYPE)).andExpect(status().isOk()).andReturn().getResponse()
        .getContentAsString();
    //when
    String isGeneratedCorrect = this.mockMvc
        .perform(get(VALIDATE_TOKEN_PATH + "/" + generatedToken).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    //then
    assertTrue(Boolean.valueOf(isGeneratedCorrect));
  }

  @Test
  public void shouldReturnErrorCausedByInCorrectToken() throws Exception {
    //given
    Mockito.when(userService.usernameExist(user.getUsername())).thenReturn(true);
    Mockito.when(userService.validateUser(user.getUsername(), user.getPassword())).thenReturn(true);
    String incorrectToken = "wrongToken1234";
    //when
    String isIncorrectTokenValid = this.mockMvc
        .perform(get(VALIDATE_TOKEN_PATH + "/" + incorrectToken).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    //then
    assertFalse(Boolean.valueOf(isIncorrectTokenValid));
  }
}
