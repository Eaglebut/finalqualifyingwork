package ru.sfedu.finalqualifyingwork.repository.interfaces;

import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Optional;

public interface TaskGroupDao {

  Optional<TaskGroup> getTaskGroup(long id);

  Statuses createTaskGroup(TaskGroup taskGroup);

  Statuses editTaskGroup(TaskGroup taskGroup);

  Statuses deleteTaskGroup(long id);

}
