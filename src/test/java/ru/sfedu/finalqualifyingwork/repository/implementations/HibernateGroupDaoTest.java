package ru.sfedu.finalqualifyingwork.repository.implementations;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.util.EntityFactory;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.HibernateUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

@Slf4j
class HibernateGroupDaoTest {

  private final EntityFactory factory = new EntityFactory(new BCryptPasswordEncoder(12));
  private final HibernateUtil hibernateUtil = new HibernateUtil();
  private final GroupDao groupDao = new HibernateGroupDao(new HibernateDataUtil(hibernateUtil), hibernateUtil);
  private final UserDao userDao = new HibernateUserDao(new HibernateDataUtil(hibernateUtil));

  @Test
  void getGroup() throws NotFoundException {
    var groupList = factory.generateGroup(10);
    var user = factory.generateUser(1)
            .stream()
            .findAny()
            .orElseThrow(() -> new NotFoundException("user list is empty"));
    assertEquals(Statuses.SUCCESS, userDao.saveUser(user));
    groupList.forEach(group -> {
      group.getMemberList().put(user, UserRole.CREATOR);
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(group));
      assertEquals(group, groupDao.getGroup(group.getId()).orElseGet(Group::new));
    });
    var usersGroupList = groupDao.getGroup(user);
    assertNotNull(usersGroupList);
    assertFalse(usersGroupList.isEmpty());
    assertEquals(groupList, usersGroupList);
    hibernateUtil.getSessionFactory().close();
  }

  @Test
  void createGroup() throws NotFoundException {
    var groupList = factory.generateGroup(10);
    var user = factory.generateUser(1)
            .stream()
            .findAny()
            .orElseThrow(() -> new NotFoundException("user list is empty"));
    assertEquals(Statuses.SUCCESS, userDao.saveUser(user));
    groupList.forEach(group -> {
      group.getMemberList().put(user, UserRole.CREATOR);
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(group));
      assertEquals(group, groupDao.getGroup(group.getId()).orElseGet(Group::new));
    });
    hibernateUtil.getSessionFactory().close();
  }

  @Test
  void editGroup() throws NotFoundException {
    var groupList = factory.generateGroup(10);
    var user = factory.generateUser(1)
            .stream()
            .findAny()
            .orElseThrow(() -> new NotFoundException("user list is empty"));
    assertEquals(Statuses.SUCCESS, userDao.saveUser(user));
    groupList.forEach(group -> {
      group.getMemberList().put(user, UserRole.CREATOR);
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(group));
      assertEquals(group, groupDao.getGroup(group.getId()).orElseGet(Group::new));
      group.setName(group.getName() + "edited");
      assertEquals(Statuses.SUCCESS, groupDao.editGroup(group));
      assertEquals(group, groupDao.getGroup(group.getId()).orElseGet(Group::new));
    });
    hibernateUtil.getSessionFactory().close();
  }

  @Test
  void deleteGroup() throws NotFoundException {
    var groupList = factory.generateGroup(10);
    var user = factory.generateUser(1)
            .stream()
            .findAny()
            .orElseThrow(() -> new NotFoundException("user list is empty"));
    assertEquals(Statuses.SUCCESS, userDao.saveUser(user));
    groupList.forEach(group -> {
      group.getMemberList().put(user, UserRole.CREATOR);
      assertEquals(Statuses.SUCCESS, groupDao.createGroup(group));
      assertEquals(group, groupDao.getGroup(group.getId()).orElseGet(Group::new));
      assertEquals(Statuses.SUCCESS, groupDao.deleteGroup(group.getId()));
      assertTrue(groupDao.getGroup(group.getId()).isEmpty());
    });
    hibernateUtil.getSessionFactory().close();
  }

}