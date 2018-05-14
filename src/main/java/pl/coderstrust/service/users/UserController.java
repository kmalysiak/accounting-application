package pl.coderstrust.service.users;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Messages;
import pl.coderstrust.model.User;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  @ApiOperation(value = "Add user")
  public ResponseEntity addUser(@RequestBody User user) {
    List<String> userState = user.validate();
    if (!userState.isEmpty()) {
      return ResponseEntity.badRequest().body(userState);
    }
    if (userService.usernameExist(user.getUsername())) {
      return ResponseEntity.badRequest().body(Messages.USER_ALREADY_EXIST);
    }
    userService.addUser(user);
    return ResponseEntity.ok(Messages.USER_ADDED);
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity getUsers() {
    return ResponseEntity.ok().body(userService.getUsers());
  }

  @RequestMapping(value = "/{username}", method = RequestMethod.PUT)
  @ApiOperation(value = "Edit user")
  public ResponseEntity editUser(@RequestBody User user) {
    List<String> userState = user.validate();
    if (!userState.isEmpty()) {
      return ResponseEntity.badRequest().body(userState);
    }
    if (!userService.usernameExist(user.getUsername())) {
      return ResponseEntity.badRequest().body(Messages.USER_NOT_EXIST);
    }
    userService.updateUser(user);
    return ResponseEntity.ok(Messages.USER_ADDED);
  }

  @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Remove user by username.")
  public ResponseEntity removeUser(
      @PathVariable("username") String username) {
    if (!userService.usernameExist(username)) {
      return ResponseEntity.badRequest().body(Messages.USER_NOT_EXIST);
    }
    userService.deleteUser(username);
    return ResponseEntity.ok().body(Messages.USER_DELETED);
  }

  @RequestMapping(value = "/validate", method = RequestMethod.POST)
  @ApiOperation(value = "Check if user credentials are valid.")
  public ResponseEntity validateUser(
      @RequestBody User userToValidate) {
    List<String> userState = userToValidate.validate();
    if (!userState.isEmpty()) {
      return ResponseEntity.badRequest().body(userState);
    }
    if (!userService.usernameExist(userToValidate.getUsername())) {
      return ResponseEntity.badRequest().body(Messages.USER_NOT_EXIST);
    }
    return ResponseEntity.ok()
        .body(userService.validateUser(userToValidate.getUsername(), userToValidate.getPassword()));
  }
}