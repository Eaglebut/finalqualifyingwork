package ru.sfedu.finalqualifyingwork.rest.api.v1;


import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.sfedu.finalqualifyingwork.model.Group;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskGroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.taskgroup.GetTaskGroupDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.taskgroup.PostTaskGroupDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/group/{groupId}")
@AllArgsConstructor
@PreAuthorize("hasAuthority('user:all')")
@CrossOrigin(origins = "*")
public class TaskGroupsController {

  private final UserDao userDao;
  private final GroupDao groupDao;
  private final JwtTokenProvider jwtTokenProvider;
  private final TaskGroupDao taskGroupDao;

  private HttpClientErrorException createHttpException(HttpStatus status, String message) throws HttpClientErrorException {
    return HttpClientErrorException.create(status, message, HttpHeaders.EMPTY, null, null);
  }

  private User getUser(String token) throws HttpClientErrorException {
    return userDao.getUser(jwtTokenProvider.getUsername(token))
            .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "user not founded"));
  }

  private Group getGroup(long groupId, User user) throws HttpClientErrorException {
    var group = groupDao.getGroup(groupId).orElseThrow(() ->
            createHttpException(HttpStatus.NOT_FOUND, "group not founded"));
    if (!group.getMemberList().containsKey(user)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "User is not a part of this group");
    }
    return group;
  }

  private TaskGroup getTaskGroup(long taskGroupId, Group group) {
    var taskGroup = taskGroupDao.getTaskGroup(taskGroupId).orElseThrow(() ->
            createHttpException(HttpStatus.NOT_FOUND, "Task group not founded"));
    if (!group.getTaskGroups().contains(taskGroup)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "Task group is not a part of this group");
    }
    return taskGroup;
  }

  private void checkUserPermissions(User user, Group group) throws HttpClientErrorException {
    if (group.getMemberList().get(user).equals(UserRole.MEMBER) ||
            group.getMemberList().get(user).equals(UserRole.INVITED)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to create task group");
    }
  }


  @GetMapping("taskGroups")
  public ResponseEntity<?> getGroupTaskGroups(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                              @PathVariable long groupId) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      return ResponseEntity.ok(group.getTaskGroups().stream().map(GetTaskGroupDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException.BadRequest exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PostMapping("taskGroup")
  public ResponseEntity<?> createTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                           @PathVariable long groupId,
                                           @RequestBody PostTaskGroupDto groupDto) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);

      var taskGroupList = group.getTaskGroups();

      if (groupDto.getPosition() > group.getTaskGroups().size() || groupDto.getPosition() < 0) {
        groupDto.setPosition(group.getTaskGroups().size());
      }
      TaskGroup taskGroup = groupDto.toTaskGroup();
      taskGroup.setOwnerGroup(group);
      group.getTaskGroups().add(groupDto.getPosition(), taskGroup);
      for (int i = taskGroup.getPosition(); i < taskGroupList.size(); i++) {
        taskGroupList.get(i).setPosition(i);
      }
      if (!taskGroupDao.createTaskGroup(taskGroup).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.BAD_REQUEST, "Invalid task group");
      }
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating group information");
      }
      return ResponseEntity.ok(group.getTaskGroups().stream().map(GetTaskGroupDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PutMapping("taskGroup/{taskGroupId}")
  public ResponseEntity<?> editTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                         @PathVariable long groupId,
                                         @PathVariable long taskGroupId,
                                         @RequestBody PostTaskGroupDto groupDto) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var taskGroupList = group.getTaskGroups();
      taskGroupList.remove(taskGroup);
      if (groupDto.getPosition() > group.getTaskGroups().size() || groupDto.getPosition() < 0) {
        groupDto.setPosition(group.getTaskGroups().size());
      }
      taskGroup.setName(groupDto.getName());
      taskGroupList.add(groupDto.getPosition(), taskGroup);
      for (int i = 0; i < taskGroupList.size(); i++) {
        taskGroupList.get(i).setPosition(i);
      }
      if (!taskGroupDao.editTaskGroup(taskGroup).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.BAD_REQUEST, "Invalid task group");
      }
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating group information");
      }
      return ResponseEntity.ok(group.getTaskGroups().stream().map(GetTaskGroupDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @DeleteMapping("taskGroup/{taskGroupId}")
  public ResponseEntity<?> deleteTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                           @PathVariable long groupId,
                                           @PathVariable long taskGroupId) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      group.getTaskGroups().remove(taskGroup);
      if (!taskGroupDao.deleteTaskGroup(taskGroup.getId()).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during task group deleting");
      }
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during group updating");
      }
      return ResponseEntity.ok(group.getTaskGroups().stream().map(GetTaskGroupDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

}
