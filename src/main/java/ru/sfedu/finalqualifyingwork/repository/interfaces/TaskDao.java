package ru.sfedu.finalqualifyingwork.repository.interfaces;

import ru.sfedu.finalqualifyingwork.model.Task;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Optional;

public interface TaskDao {

  Optional<Task> getTask(long id);

  Statuses createTask(Task task);

  Statuses editTask(Task task);

  Statuses deleteTask(long id);

}
