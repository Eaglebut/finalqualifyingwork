package ru.sfedu.finalqualifyingwork.repository.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskGroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.util.EntityFactory;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.HibernateUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

class HibernateTaskDaoTest {

  private final EntityFactory factory = new EntityFactory(new BCryptPasswordEncoder(12));
  private final HibernateUtil hibernateUtil = new HibernateUtil();
  private final HibernateDataUtil hibernateDataUtil = new HibernateDataUtil(hibernateUtil);
  private final TaskGroupDao taskGroupDao = new HibernateTaskGroupDao(hibernateDataUtil);
  private final GroupDao groupDao = new HibernateGroupDao(hibernateDataUtil, hibernateUtil);
  private final TaskDao taskDao = new HibernateTaskDao(hibernateDataUtil);
  private final UserDao userDao = new HibernateUserDao(hibernateDataUtil);


  @Test
  void getTask() {
    var taskList = factory.generateTask(10);
    taskList.forEach(task -> {
      assertEquals(Statuses.SUCCESS, userDao.saveUser(task.getAuthor()));
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(task.getOwner().getOwnerGroup()));
      assertEquals(Statuses.SUCCESS, taskGroupDao.createTaskGroup(task.getOwner()));
      assertEquals(Statuses.SUCCESS, taskDao.createTask(task));
      assertEquals(task, taskDao.getTask(task.getId()).orElseThrow());
    });
  }

  @Test
  void createTask() {
    var taskList = factory.generateTask(10);
    taskList.forEach(task -> {
      assertEquals(Statuses.SUCCESS, userDao.saveUser(task.getAuthor()));
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(task.getOwner().getOwnerGroup()));
      assertEquals(Statuses.SUCCESS, taskGroupDao.createTaskGroup(task.getOwner()));
      assertEquals(Statuses.SUCCESS, taskDao.createTask(task));
      assertEquals(task, taskDao.getTask(task.getId()).orElseThrow());
    });
  }

  @Test
  void editTask() {
    var taskList = factory.generateTask(10);
    taskList.forEach(task -> {
      assertEquals(Statuses.SUCCESS, userDao.saveUser(task.getAuthor()));
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(task.getOwner().getOwnerGroup()));
      assertEquals(Statuses.SUCCESS, taskGroupDao.createTaskGroup(task.getOwner()));
      assertEquals(Statuses.SUCCESS, taskDao.createTask(task));
      task.setText(task.getText() + " edited");
      assertEquals(Statuses.SUCCESS, taskDao.editTask(task));
      assertEquals(task, taskDao.getTask(task.getId()).orElseThrow());
    });
  }

  @Test
  void deleteTask() {
    var taskList = factory.generateTask(10);
    taskList.forEach(task -> {
      assertEquals(Statuses.SUCCESS, userDao.saveUser(task.getAuthor()));
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(task.getOwner().getOwnerGroup()));
      assertEquals(Statuses.SUCCESS, taskGroupDao.createTaskGroup(task.getOwner()));
      assertEquals(Statuses.SUCCESS, taskDao.createTask(task));
      assertEquals(Statuses.SUCCESS, taskDao.deleteTask(task.getId()));
      assertTrue(taskDao.getTask(task.getId()).isEmpty());
    });
  }
}