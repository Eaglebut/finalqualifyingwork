package ru.sfedu.finalqualifyingwork.repository.interfaces;

import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.List;
import java.util.Optional;

public interface GroupDao {

  List<Group> getGroup(User user);

  Optional<Group> getGroup(long id);

  Statuses createGroup(Group group);

  Statuses editGroup(Group group);

  Statuses deleteGroup(long id);
}
