package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.HibernateUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class HibernateGroupDao implements GroupDao {

  private final HibernateDataUtil hibernateDataUtil;
  private final HibernateUtil hibernateUtil;

  @Override
  public List<Group> getGroup(User user) {
    return hibernateUtil
            .getSessionFactory()
            .openSession()
            .createSQLQuery("select g.* from groups_memberlist inner join groups g on g.id = groups_memberlist.groups_id where user_id = " + user.getId())
            .addEntity(Group.class)
            .list();
  }

  @Override
  public Optional<Group> getGroup(long id) {
    return hibernateDataUtil.getEntityById(Group.class, id);
  }

  @Override
  public Statuses createGroup(Group group) {
    group.setCreated(new Date());
    group.setLastUpdated(new Date());
    return hibernateDataUtil.createEntity(group);
  }

  @Override
  public Statuses editGroup(Group group) {
    group.setLastUpdated(new Date());
    return hibernateDataUtil.updateEntity(group);
  }

  @Override
  public Statuses deleteGroup(long id) {
    return hibernateDataUtil.deleteEntity(Group.class, id);
  }
}
