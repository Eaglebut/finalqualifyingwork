package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskGroupDao;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateTaskGroupDao implements TaskGroupDao {

  private final HibernateDataUtil hibernateDataUtil;


  @Override
  public Optional<TaskGroup> getTaskGroup(long id) {
    return hibernateDataUtil.getEntityById(TaskGroup.class, id);
  }

  @Override
  public Statuses createTaskGroup(TaskGroup taskGroup) {
    taskGroup.setCreated(new Date());
    taskGroup.setLastUpdated(new Date());
    return hibernateDataUtil.createEntity(taskGroup);
  }

  @Override
  public Statuses editTaskGroup(TaskGroup taskGroup) {
    taskGroup.setLastUpdated(new Date());
    return hibernateDataUtil.updateEntity(taskGroup);
  }

  @Override
  public Statuses deleteTaskGroup(long id) {
    return hibernateDataUtil.deleteEntity(TaskGroup.class, id);
  }
}
