package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Task;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.PublicUserDto;

@Data
@NoArgsConstructor
@ApiModel
public class GetTaskDto {

  private long id;
  private String name;
  private String text;
  private PublicUserDto author;
  private int position;

  public GetTaskDto(Task task) {
    id = task.getId();
    name = task.getName();
    text = task.getText();
    author = new PublicUserDto(task.getAuthor());
    position = getPosition();
  }

  public Task toTask() {
    Task task = new Task();
    task.setId(id);
    task.setName(name);
    task.setText(text);
    task.setPosition(position);
    task.setAuthor(author.toUser());
    return task;
  }
}
