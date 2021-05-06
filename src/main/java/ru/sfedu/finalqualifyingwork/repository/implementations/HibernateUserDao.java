package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateUserDao implements UserDao {

  private final HibernateDataUtil hibernateDataUtil;

  @Override
  public Optional<User> getUser(@NonNull String email) {
    return hibernateDataUtil.executeQuerySingle(User.class, "from User where email = ?1", email);
  }

  @Override
  public Optional<User> getUser(long id) {
    return hibernateDataUtil.getEntityById(User.class, id);
  }

  @Override
  public Statuses saveUser(@NonNull User user) {
    return hibernateDataUtil.createEntity(user);
  }

  @Override
  public Statuses editUser(@NonNull User user) {
    user.setLastUpdated(new Date());
    return hibernateDataUtil.updateEntity(user);
  }
}
