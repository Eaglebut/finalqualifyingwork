package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Task;

@Data
@NoArgsConstructor
@ApiModel
public class GetTaskDto {

  public GetTaskDto(Task task) {

  }

  public Task toTask() {
    return null;
  }
}
