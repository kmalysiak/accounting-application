package pl.coderstrust.service.tokens;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.coderstrust.model.User;
import pl.coderstrust.service.users.UserService;

public class UserServiceTest {

  private static UserService userService;

  private static User lukasz;
  private static User sebastian;
  private static User krzysiek;
  private static User piotr;

  @BeforeClass
  public static void setup() {
    userService = new UserService();
    lukasz = new User("Lukasz", "lukaszPassword");
    sebastian = new User("Sebastian", "sebaPassword");
    krzysiek = new User("Krzysiek", "krzysiekPassword");
    piotr = new User("Piotr", "piotrPassword");
  }

  @Test
  public void shouldAddUsers() {
    //when
    userService.addUser(lukasz);
    userService.addUser(sebastian);
    userService.addUser(piotr);
    userService.addUser(krzysiek);
    //then
    Assert.assertTrue(userService.usernameExist("Lukasz"));
    Assert.assertTrue(userService.usernameExist("Sebastian"));
    Assert.assertTrue(userService.usernameExist("Krzysiek"));
    Assert.assertTrue(userService.usernameExist("Piotr"));
  }

  @Test
  public void updateUser() {
    //given
    userService.addUser(lukasz);
    String lukaszNewPassword = "123456";
    lukasz.setPassword("123456");
    //when
    userService.updateUser(lukasz);
    //then
    Assert.assertTrue(userService.validateUser("Lukasz", lukaszNewPassword));

  }

  @Test
  public void deleteUser() {
    //given
    userService.addUser(lukasz);
    userService.addUser(sebastian);
    userService.addUser(piotr);
    userService.addUser(krzysiek);
    //when
    userService.deleteUser("Sebastian");
    //then
    Assert.assertFalse(userService.usernameExist("Sebastian"));

  }

  @Test
  public void usernameExist() {
    //given
    userService.addUser(lukasz);
    userService.addUser(sebastian);
    userService.addUser(piotr);
    userService.addUser(krzysiek);
    //then
    Assert.assertTrue(userService.usernameExist("Lukasz"));
    Assert.assertTrue(userService.usernameExist("Sebastian"));
    Assert.assertTrue(userService.usernameExist("Krzysiek"));
    Assert.assertTrue(userService.usernameExist("Piotr"));
    Assert.assertFalse(userService.usernameExist("Wiola"));
  }

  @Test
  public void validateUser() {
    //given
    userService.addUser(lukasz);
    userService.addUser(sebastian);
    userService.addUser(piotr);
    userService.addUser(krzysiek);
    //then
    Assert.assertTrue(userService.validateUser("Piotr", "piotrPassword"));
    Assert.assertTrue(userService.validateUser("Sebastian", "sebaPassword"));
    Assert.assertTrue(userService.validateUser("Krzysiek", "krzysiekPassword"));
  }
}