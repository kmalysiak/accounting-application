package pl.coderstrust.service.tokens;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.model.Messages;
import pl.coderstrust.model.User;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser
public class UserControllerIntegrationTest {

  private static final String ADD_USER_METHOD = "addUser";
  private static final String EDIT_USER_METHOD = "editUser";
  private static final String DELETE_USER = "removeUser";
  private static final String GET_USER = "getUsers";
  private static final String VALIDATE_USER = "validateUser";
  private static final String DEFAULT_PATH = "/users";
  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;

  private User lukasz = new User("Lukasz", "lukaszPassword");
  private User sebastian = new User("Sebastian", "sebaPassword");
  private User krzysiek = new User("Krzysiek", "krzysiekPassword");
  private User piotr = new User("Piotr", "piotrPassword");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldAddUser() throws Exception {
    //when
    addUser(lukasz);
    addUser(sebastian);
    addUser(krzysiek);
    addUser(piotr);
    //then
    List<String> usernames = getListWithAllUsernames();

    assertThat(usernames.size(), is(4));
    assertTrue(usernames.contains(lukasz.getUsername()));
    assertTrue(usernames.contains(sebastian.getUsername()));
    assertTrue(usernames.contains(krzysiek.getUsername()));
    assertTrue(usernames.contains(piotr.getUsername()));
  }

  @Test
  public void shouldReturnErrorCausedByIncompleteUser() throws Exception {
    //given
    User incompleteUser = new User("Tomek", "1234");
    incompleteUser.setPassword(null);
    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(mapper.writeValueAsString(incompleteUser))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_USER_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByUserAlreadyExist() throws Exception {
    //given
    addUser(lukasz);
    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(mapper.writeValueAsString(lukasz))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_USER_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnUsernamesList() throws Exception {
    //given
    addUser(lukasz);
    addUser(sebastian);
    addUser(krzysiek);
    addUser(piotr);
    //when
    List<String> usernames = getListWithAllUsernames();
    //then
    assertThat(usernames.size(), is(4));
    assertTrue(usernames.contains(lukasz.getUsername()));
    assertTrue(usernames.contains(sebastian.getUsername()));
    assertTrue(usernames.contains(krzysiek.getUsername()));
    assertTrue(usernames.contains(piotr.getUsername()));
  }

  @Test
  public void shouldDeleteExistingUserByUsername() throws Exception {
    //given
    addUser(lukasz);
    addUser(sebastian);
    addUser(krzysiek);
    addUser(piotr);
    //when
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/" + sebastian.getUsername())
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(DELETE_USER))
        .andExpect(status().isOk());
    //then
    List<String> usernames = getListWithAllUsernames();

    assertThat(usernames.size(), is(3));
    assertTrue(usernames.contains(lukasz.getUsername()));
    assertFalse(usernames.contains(sebastian.getUsername()));
    assertTrue(usernames.contains(krzysiek.getUsername()));
    assertTrue(usernames.contains(piotr.getUsername()));
  }

  @Test
  public void shouldEditExistingUser() throws Exception {
    //given
    addUser(lukasz);
    addUser(sebastian);
    addUser(krzysiek);
    addUser(piotr);
    lukasz.setPassword("1234");
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + lukasz.getUsername())
            .content(mapper.writeValueAsString(lukasz))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(EDIT_USER_METHOD))
        .andExpect(status().isOk());
    //then
    String response = this.mockMvc
        .perform(post(DEFAULT_PATH + "/validate")
            .content(mapper.writeValueAsString(lukasz))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(VALIDATE_USER))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    assertTrue(Boolean.valueOf(response));
  }

  @Test
  public void shouldReturnErrorCausedByIncompleteUserInUserEditMethod() throws Exception {
    //given
    User incompleteUser = new User("Tomek", "1234");
    incompleteUser.setPassword(null);
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + incompleteUser.getUsername())
            .content(mapper.writeValueAsString(incompleteUser))
            .contentType(JSON_CONTENT_TYPE))

        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedNotExistingUser() throws Exception {
    //when
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/" + "NotExistingUser")
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByUserNotExist() throws Exception {
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + lukasz.getUsername())
            .content(mapper.writeValueAsString(lukasz))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(EDIT_USER_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldValidateExistingUser() throws Exception {
    //given
    addUser(lukasz);
    addUser(sebastian);
    //when
    String ifLukaszIsValidUser = this.mockMvc
        .perform(post(DEFAULT_PATH + "/validate")
            .content(mapper.writeValueAsString(lukasz))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(VALIDATE_USER))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    String ifSebastianIsValidUser = this.mockMvc
        .perform(post(DEFAULT_PATH + "/validate")
            .content(mapper.writeValueAsString(sebastian))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(VALIDATE_USER))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    assertTrue(Boolean.valueOf(ifLukaszIsValidUser));
    assertTrue(Boolean.valueOf(ifSebastianIsValidUser));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingUser() throws Exception {
    //when
    String ifRandomUserIsValid = this.mockMvc
        .perform(post(DEFAULT_PATH + "/validate")
            .content(mapper.writeValueAsString(
                new User("Random", "randomPassword")))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(VALIDATE_USER))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();
    //then
    assertTrue(ifRandomUserIsValid.equals(Messages.USER_NOT_EXIST));
  }

  @Test
  public void shouldReturnErrorCausedByWrongCredentials() throws Exception {
    //given
    addUser(sebastian);
    sebastian.setPassword("1234");
    //when
    String ifSebastianWithWrongPasswordIsValidUser = this.mockMvc
        .perform(post(DEFAULT_PATH + "/validate")
            .content(mapper.writeValueAsString(sebastian))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(VALIDATE_USER))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    //then
    assertFalse(Boolean.valueOf(ifSebastianWithWrongPasswordIsValidUser));
  }

  private void addUser(User user) throws Exception {
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(mapper.writeValueAsString(user))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_USER_METHOD))
        .andExpect(status().isOk());
  }

  private List<String> getUsernamesFromResponse(String response) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    return mapper.readValue(
        response,
        mapper.getTypeFactory().constructCollectionType(List.class, String.class));
  }

  private List<String> getListWithAllUsernames() throws Exception {
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_USER))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getUsernamesFromResponse(response);
  }
}


