package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.group;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.model.enums.GroupType;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.PublicUserDto;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ApiModel
public class GetGroupDto {
  private long id;
  private String name;
  private GroupType groupType;
  private Map<PublicUserDto, UserRole> memberList;
  private Set<GetGroupDto> subGroups;
  private List<TaskGroup> taskGroups;

  public GetGroupDto(Group group) {
    id = group.getId();
    name = group.getName();
    memberList = new HashMap<>();
    group.getMemberList().forEach((user, userRole) -> memberList.put(new PublicUserDto(user), userRole));
    subGroups = new HashSet<>();
    group.getSubGroups().forEach(subGroup -> subGroups.add(new GetGroupDto(subGroup)));
    taskGroups = group.getTaskGroups();
    groupType = group.getGroupType();
  }

  public Group toGroup() {
    var group = new Group();
    group.setName(name);
    group.setGroupType(groupType);
    group.setMemberList(new HashMap<>());
    memberList.forEach((publicUserDto, userRole) -> group.getMemberList().put(publicUserDto.toUser(), userRole));
    group.setSubGroups(subGroups.stream().map(GetGroupDto::toGroup).collect(Collectors.toSet()));
    group.setTaskGroups(taskGroups);
    return group;
  }

}
