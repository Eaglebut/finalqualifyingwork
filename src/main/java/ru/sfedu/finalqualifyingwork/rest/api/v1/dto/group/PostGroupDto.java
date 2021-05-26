package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.group;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Group;

@Data
@NoArgsConstructor
@ApiModel
public class PostGroupDto {
  private String name;
  private String description;
  private long baseGroupId;

  public PostGroupDto(Group group) {
    name = group.getName();
    description = group.getDescription();
    baseGroupId = group.getBaseGroup() != null ? group.getBaseGroup().getId() : 0;
  }

  public Group toGroup() {
    Group group = new Group();
    group.setName(name);
    group.setDescription(description);
    return group;
  }
}
