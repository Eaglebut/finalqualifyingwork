package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.Task;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskDao;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateTaskDao implements TaskDao {

  private final HibernateDataUtil hibernateDataUtil;


  @Override
  public Optional<Task> getTask(long id) {
    return hibernateDataUtil.getEntityById(Task.class, id);
  }

  @Override
  public Statuses createTask(Task task) {
    task.setCreated(new Date());
    task.setLastUpdated(new Date());
    return hibernateDataUtil.createEntity(task);
  }

  @Override
  public Statuses editTask(Task task) {
    task.setLastUpdated(new Date());
    return hibernateDataUtil.updateEntity(task);
  }

  @Override
  public Statuses deleteTask(long id) {
    return hibernateDataUtil.deleteEntity(Task.class, id);
  }
}
