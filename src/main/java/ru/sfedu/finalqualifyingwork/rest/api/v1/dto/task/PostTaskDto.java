package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Task;

@Data
@NoArgsConstructor
@ApiModel
public class PostTaskDto {

  private String name;
  private String text;
  private int position;

  public PostTaskDto(Task task) {
    name = task.getName();
    text = task.getText();
    position = task.getPosition();
  }

  public Task toTask() {
    Task task = new Task();
    task.setName(name);
    task.setText(text);
    task.setPosition(position);
    return task;
  }
}
