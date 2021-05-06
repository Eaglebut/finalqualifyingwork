package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.taskgroup;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task.GetTaskDto;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ApiModel
public class GetTaskGroupDto {
  private long id;
  private String name;
  private List<GetTaskDto> taskList;
  private int position;

  public GetTaskGroupDto(TaskGroup taskGroup) {
    id = taskGroup.getId();
    name = taskGroup.getName();
    taskList = taskGroup.getTaskList().stream().map(GetTaskDto::new).collect(Collectors.toList());
    position = taskGroup.getPosition();
  }

  public TaskGroup toTaskGroup() {
    var taskGroup = new TaskGroup();
    taskGroup.setId(id);
    taskGroup.setName(name);
    taskGroup.setTaskList(taskList.stream().map(GetTaskDto::toTask).collect(Collectors.toList()));
    taskGroup.setPosition(position);
    return taskGroup;
  }
}
