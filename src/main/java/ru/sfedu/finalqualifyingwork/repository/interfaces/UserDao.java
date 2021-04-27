package ru.sfedu.finalqualifyingwork.repository.interfaces;

import lombok.NonNull;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Optional;

public interface UserDao {

  Optional<User> getUser(@NonNull String email);

  Optional<User> getUser(long id);

  Statuses saveUser(@NonNull User user);

  Statuses editUser(@NonNull User user);

}
