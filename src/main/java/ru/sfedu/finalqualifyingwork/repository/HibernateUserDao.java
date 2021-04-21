package ru.sfedu.finalqualifyingwork.repository;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.Status;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.List;
import java.util.Optional;

@Service("userDaoImpl")
public class HibernateUserDao implements UserDao{

  @Override
  public Optional<User> getUser(@NonNull String email) {
      return HibernateDataUtil.executeQuerySingle(User.class,"from User where email = ?1", email);
    }

  @Override
  public Optional<User> getUser(long id) {
    return HibernateDataUtil.executeQuerySingle(User.class,"from User where id = ?1", id);
  }

  @Override
  public List<User> getUserList() {
    return HibernateDataUtil.executeQueryList(User.class, "from User");
  }

  @Override
  public Statuses deleteUser(long id) {
    return HibernateDataUtil.deleteEntity(User.class, id);
  }

  @Override
  public Statuses saveUser(@NonNull User user) {
    return HibernateDataUtil.createEntity(user);
  }

  @Override
  public Statuses banUser(long id) {
    var optUser = getUser(id);
    if (optUser.isEmpty()){
      return Statuses.NOT_FOUNDED;
    }
    var user = optUser.get();
    user.setStatus(Status.BANNED);
    editUser(user);
    return Statuses.SUCCESS;
  }

  @Override
  public Statuses editUser(@NonNull User user) {
    return HibernateDataUtil.updateEntity(user);
  }
}
