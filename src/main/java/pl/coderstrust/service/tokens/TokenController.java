package pl.coderstrust.service.tokens;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Messages;
import pl.coderstrust.model.Token;
import pl.coderstrust.model.User;
import pl.coderstrust.service.users.UserService;

import java.util.List;

@RestController
@RequestMapping("tokens")
public class TokenController {

  private TokenService tokenService;
  private UserService userService;

  @Autowired
  public TokenController(TokenService tokenService,
      UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;
  }

  @RequestMapping(value = "/generate", method = RequestMethod.POST)
  @ApiOperation(value = "Generate token")
  public ResponseEntity generateToken(@RequestBody User userToValidate) {
    if (!userService.usernameExist(userToValidate.getUsername())) {
      return ResponseEntity.badRequest().body(Messages.USER_NOT_EXIST);
    }
    if (!userService.validateUser(userToValidate.getUsername(), userToValidate.getPassword())) {
      return ResponseEntity.badRequest().body(Messages.USER_WRONG_PASSWORD);
    }
    return ResponseEntity.ok().body(tokenService.generateToken());
  }

  @RequestMapping(value = "/validate/{token}", method = RequestMethod.GET)
  @ApiOperation(value = "Validate token")
  public ResponseEntity validateToken(@PathVariable String token) {
    return ResponseEntity.ok().body(tokenService.validateToken(token));
  }

}
