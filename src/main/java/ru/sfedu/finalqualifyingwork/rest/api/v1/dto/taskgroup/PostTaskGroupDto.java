package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.taskgroup;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;

@Data
@NoArgsConstructor
@ApiModel
public class PostTaskGroupDto {
  private String name;
  private int position;

  public PostTaskGroupDto(TaskGroup taskGroup) {
    name = taskGroup.getName();
    position = taskGroup.getPosition();
  }

  public TaskGroup toTaskGroup() {
    var taskGroup = new TaskGroup();
    taskGroup.setName(name);
    taskGroup.setPosition(position);
    return taskGroup;
  }
}
