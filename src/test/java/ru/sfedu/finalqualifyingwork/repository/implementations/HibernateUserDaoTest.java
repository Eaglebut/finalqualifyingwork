package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.util.EntityFactory;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.HibernateUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

@Slf4j
class HibernateUserDaoTest {

  private final EntityFactory factory = new EntityFactory(new BCryptPasswordEncoder(12));
  private final HibernateUtil hibernateUtil = new HibernateUtil();
  private final UserDao userDao = new HibernateUserDao(new HibernateDataUtil(hibernateUtil));

  @Test
  void getUser() {
    var userList = factory.generateUser(10);
    userList.forEach(user -> assertEquals(Statuses.SUCCESS, userDao.saveUser(user)));
    userList.forEach(user -> assertEquals(user, userDao.getUser(user.getId()).orElseGet(User::new)));
    userList.forEach(user -> assertEquals(user, userDao.getUser(user.getEmail()).orElseGet(User::new)));
    hibernateUtil.getSessionFactory().close();
  }

  @Test
  void saveUser() {
    var userList = factory.generateUser(10);
    userList.forEach(user -> assertEquals(Statuses.SUCCESS, userDao.saveUser(user)));
    userList.forEach(user -> assertEquals(user, userDao.getUser(user.getId()).orElseGet(User::new)));
    hibernateUtil.getSessionFactory().close();
  }

  @Test
  void editUser() {
    var userList = factory.generateUser(10);
    userList.forEach(user -> assertEquals(Statuses.SUCCESS, userDao.saveUser(user)));
    userList.forEach(user -> user.setName(user.getName() + " edited"));
    userList.forEach(userDao::editUser);
    userList.forEach(user -> assertEquals(user, userDao.getUser(user.getId()).orElseGet(User::new)));
    hibernateUtil.getSessionFactory().close();
  }
}