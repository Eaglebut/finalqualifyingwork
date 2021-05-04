package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskGroupDao;
import ru.sfedu.finalqualifyingwork.util.EntityFactory;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.HibernateUtil;

@Slf4j
class HibernateTaskGroupDaoTest {

  private final EntityFactory factory = new EntityFactory(new BCryptPasswordEncoder(12));
  private final HibernateUtil hibernateUtil = new HibernateUtil();
  private final TaskGroupDao taskGroupDao = new HibernateTaskGroupDao(new HibernateDataUtil(hibernateUtil));


  @Test
  void getTaskGroup() {
    var taskGroupList = factory.generateTaskGroup(10);
    taskGroupList.forEach(taskGroupDao::createTaskGroup);
    taskGroupList.forEach(taskGroup -> assertEquals(taskGroup, taskGroupDao
            .getTaskGroup(taskGroup.getId())
            .orElse(new TaskGroup())));
  }

  @Test
  void createTaskGroup() {
    var taskGroupList = factory.generateTaskGroup(10);
    taskGroupList.forEach(taskGroupDao::createTaskGroup);
    taskGroupList.forEach(taskGroup -> assertEquals(taskGroup, taskGroupDao
            .getTaskGroup(taskGroup.getId())
            .orElse(new TaskGroup())));
  }

  @Test
  void editTaskGroup() {
    var taskGroupList = factory.generateTaskGroup(10);
    taskGroupList.forEach(taskGroupDao::createTaskGroup);
    taskGroupList.forEach(taskGroup -> taskGroup.setName(taskGroup.getName() + " edited"));
    taskGroupList.forEach(taskGroupDao::editTaskGroup);
    taskGroupList.forEach(taskGroup -> assertEquals(taskGroup, taskGroupDao
            .getTaskGroup(taskGroup.getId())
            .orElse(new TaskGroup())));
  }

  @Test
  void deleteTaskGroup() {
    var taskGroupList = factory.generateTaskGroup(10);
    taskGroupList.forEach(taskGroupDao::createTaskGroup);
    taskGroupList.forEach(taskGroup -> assertEquals(taskGroup, taskGroupDao
            .getTaskGroup(taskGroup.getId())
            .orElse(new TaskGroup())));
    taskGroupList.forEach(taskGroup -> taskGroupDao.deleteTaskGroup(taskGroup.getId()));
    taskGroupList.forEach(taskGroup -> assertTrue(taskGroupDao.getTaskGroup(taskGroup.getId()).isEmpty()));
  }
}