package ru.sfedu.finalqualifyingwork.repository;

import lombok.NonNull;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.List;
import java.util.Optional;

public interface UserDao {

  Optional<User> getUser(@NonNull String email);
  Optional<User> getUser(long id);
  List<User> getUserList();

  Statuses saveUser(@NonNull User user);

  Statuses banUser(long id);

  Statuses deleteUser(long id);

  Statuses editUser(@NonNull User user);

}
