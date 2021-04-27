package ru.sfedu.finalqualifyingwork.util;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.model.enums.Role;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class EntityFactory {

  private final PasswordEncoder encoder;

  public List<User> generateUser(int amount) {
    List<User> userList = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      User user = new User();
      user.setEmail("testUser№" + i + "@" + new Date() + ".com");
      user.setPassword(encoder.encode("user"));
      user.setName("testUserName№" + i);
      user.setSurname("testUserSurname№" + i);
      user.setStatus(AccountStatus.ACTIVE);
      user.setRole(Role.USER);
      userList.add(user);
    }
    return userList;
  }

}
