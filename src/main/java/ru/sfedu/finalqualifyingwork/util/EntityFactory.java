package ru.sfedu.finalqualifyingwork.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.model.enums.Role;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class EntityFactory {

  private final PasswordEncoder encoder;

  public List<User> generateUser(int amount) {
    log.debug("starting generate users");
    List<User> userList = new ArrayList<>();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    for (int i = 0; i < amount; i++) {
      User user = new User();
      user.setEmail("testUser№" + i + "@" + dateFormat.format(new Date()) + ".com");
      user.setPassword(encoder.encode("user"));
      user.setName("testUserName№" + i);
      user.setSurname("testUserSurname№" + i);
      user.setStatus(AccountStatus.ACTIVE);
      user.setRole(Role.USER);
      userList.add(user);
    }
    log.debug("finishing generate users");
    return userList;
  }

  public List<Group> generateGroup(int amount) {
    log.debug("starting generate groups");
    List<Group> groupList = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      Group group = new Group();
      group.setName("testGroupName№" + i);
      groupList.add(group);
    }
    log.debug("finishing generate groups");
    return groupList;
  }

}
