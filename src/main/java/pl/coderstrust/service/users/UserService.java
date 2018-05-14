package pl.coderstrust.service.users;


import org.springframework.stereotype.Service;
import pl.coderstrust.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

  private HashMap<String, User> users = new HashMap<>();

  public void addUser(User user) {
    users.put(user.getUsername(), user);
  }

  public void updateUser(User user) {
    users.replace(user.getUsername(), user);
  }

  public void deleteUser(String username) {
    users.remove(username);
  }

  public List<String> getUsers() {
    return users.values().stream()
        .map(User::getUsername)
        .collect(Collectors.toList());
  }

  public boolean usernameExist(String username) {
    return users.keySet().contains(username);
  }

  public boolean validateUser(String username, String password) {
    return users.get(username).getPassword().equals(password);
  }
}

