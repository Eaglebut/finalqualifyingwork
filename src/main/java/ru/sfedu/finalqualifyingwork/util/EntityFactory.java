package ru.sfedu.finalqualifyingwork.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.Task;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.model.enums.GroupType;
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
      group.setGroupType(GroupType.STANDARD_GROUP);
      groupList.add(group);
    }
    log.debug("finishing generate groups");
    return groupList;
  }

  public List<TaskGroup> generateTaskGroup(int amount) {
    log.debug("starting generate task groups");
    List<TaskGroup> taskGroupList = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      TaskGroup taskGroup = new TaskGroup();
      taskGroup.setName("testTaskGroupName№" + i);
      taskGroup.setOwnerGroup(generateGroup(1).stream().findAny().orElseThrow());
      taskGroup.setPosition(i);
      taskGroupList.add(taskGroup);
    }
    log.debug("finishing generate task groups");
    return taskGroupList;
  }

  public List<Task> generateTask(int amount) {
    log.debug("starting generate tasks");
    List<Task> taskList = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      Task task = new Task();
      task.setName("testTaskName№" + i);
      task.setText("test task description № " + i);
      task.setAuthor(generateUser(1).stream().findAny().orElseThrow());
      task.setOwner(generateTaskGroup(1).stream().findAny().orElseThrow());
      task.setPosition(i);
      taskList.add(task);
    }
    log.debug("finishing generate tasks");
    return taskList;
  }


}
