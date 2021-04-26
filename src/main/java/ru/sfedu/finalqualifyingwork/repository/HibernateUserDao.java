package ru.sfedu.finalqualifyingwork.repository;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.List;
import java.util.Optional;

@Service("userDaoImpl")
@AllArgsConstructor
public class HibernateUserDao implements UserDao {

  private final HibernateDataUtil hibernateDataUtil;

  @Override
  public Optional<User> getUser(@NonNull String email) {
    return hibernateDataUtil.executeQuerySingle(User.class, "from User where email = ?1", email);
  }

  @Override
  public Optional<User> getUser(long id) {
    return hibernateDataUtil.executeQuerySingle(User.class, "from User where id = ?1", id);
  }

  @Override
  public List<User> getUserList() {
    return hibernateDataUtil.executeQueryList(User.class, "from User");
  }

  @Override
  public Statuses deleteUser(long id) {
    return hibernateDataUtil.deleteEntity(User.class, id);
  }

  @Override
  public Statuses saveUser(@NonNull User user) {
    return hibernateDataUtil.createEntity(user);
  }

  @Override
  public Statuses banUser(long id) {
    var optUser = getUser(id);
    if (optUser.isEmpty()){
      return Statuses.NOT_FOUNDED;
    }
    var user = optUser.get();
    user.setStatus(AccountStatus.BANNED);
    editUser(user);
    return Statuses.SUCCESS;
  }

  @Override
  public Statuses editUser(@NonNull User user) {
    return hibernateDataUtil.updateEntity(user);
  }
}
