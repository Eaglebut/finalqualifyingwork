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
import ru.sfedu.finalqualifyingwork.model.Task;
import ru.sfedu.finalqualifyingwork.model.TaskGroup;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.TaskGroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task.GetTaskDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task.PostTaskDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.task.PutTaskDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/group/{groupId}/taskGroup/{taskGroupId}/")
@AllArgsConstructor
@PreAuthorize("hasAuthority('user:all')")
@CrossOrigin(origins = "*")
public class TaskController {

  private final UserDao userDao;
  private final GroupDao groupDao;
  private final JwtTokenProvider jwtTokenProvider;
  private final TaskGroupDao taskGroupDao;
  private final TaskDao taskDao;

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

  private Task getTask(long taskId, TaskGroup taskGroup) {
    var task = taskDao.getTask(taskId).orElseThrow(() ->
            createHttpException(HttpStatus.NOT_FOUND, "Task not founded"));
    if (!taskGroup.getTaskList().contains(task)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "Task is not a part of this task group");
    }
    return task;
  }

  private void checkUserPermissions(User user, Group group) throws HttpClientErrorException {
    if (group.getMemberList().get(user).equals(UserRole.MEMBER) ||
            group.getMemberList().get(user).equals(UserRole.INVITED)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to create task");
    }
  }


  @GetMapping("tasks")
  public ResponseEntity<?> getTaskGroupTasks(@RequestHeader("Authorization")
                                             @ApiParam(hidden = true) String token,
                                             @PathVariable long groupId,
                                             @PathVariable long taskGroupId) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      var taskGroup = getTaskGroup(taskGroupId, group);
      return ResponseEntity.ok(taskGroup.getTaskList().stream().map(GetTaskDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException.BadRequest exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PostMapping("task")
  public ResponseEntity<?> createTask(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                      @PathVariable long groupId,
                                      @PathVariable long taskGroupId,
                                      @RequestBody PostTaskDto taskDto) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var taskList = taskGroup.getTaskList();

      if (taskDto.getPosition() > group.getTaskGroups().size() || taskDto.getPosition() < 0) {
        taskDto.setPosition(group.getTaskGroups().size());
      }
      Task task = taskDto.toTask();
      task.setAuthor(user);
      task.setOwner(taskGroup);
      taskList.add(taskDto.getPosition(), task);
      for (int i = task.getPosition(); i < taskList.size(); i++) {
        taskList.get(i).setPosition(i);
      }
      if (!taskDao.createTask(task).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.BAD_REQUEST, "Invalid task");
      }
      if (!taskGroupDao.editTaskGroup(taskGroup).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating task group information");
      }
      return ResponseEntity.ok(taskGroup.getTaskList().stream().map(GetTaskDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PutMapping("task/{taskId}")
  public ResponseEntity<?> editTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                         @PathVariable long groupId,
                                         @PathVariable long taskGroupId,
                                         @PathVariable long taskId,
                                         @RequestBody PutTaskDto taskDto) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var task = getTask(taskId, taskGroup);
      taskGroup.getTaskList().remove(task);
      if (taskDto.getBaseTaskGroupId() != taskGroupId) {
        if (taskDto.getPosition() > taskGroup.getTaskList().size() || taskDto.getPosition() < 0) {
          taskDto.setPosition(taskGroup.getTaskList().size());
        }
        if (!taskGroupDao.editTaskGroup(taskGroup).equals(Statuses.SUCCESS)) {
          throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating task group information");
        }
        taskGroup = getTaskGroup(taskDto.getBaseTaskGroupId(), group);
        task.setOwner(taskGroup);
      }
      if (taskDto.getPosition() > taskGroup.getTaskList().size() || taskDto.getPosition() < 0) {
        taskDto.setPosition(taskGroup.getTaskList().size());
      }
      task.setName(taskDto.getName());
      task.setText(taskDto.getText());
      taskGroup.getTaskList().add(taskDto.getPosition(), task);
      for (int i = 0; i < taskGroup.getTaskList().size(); i++) {
        taskGroup.getTaskList().get(i).setPosition(i);
      }
      if (!taskDao.editTask(task).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.BAD_REQUEST, "Invalid task");
      }
      if (!taskGroupDao.editTaskGroup(taskGroup).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating task group information");
      }
      return ResponseEntity.ok(taskGroup.getTaskList().stream().map(GetTaskDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @DeleteMapping("task/{taskId}")
  public ResponseEntity<?> deleteTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                           @PathVariable long groupId,
                                           @PathVariable long taskGroupId,
                                           @PathVariable long taskId) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var task = getTask(taskId, taskGroup);
      taskGroup.getTaskList().remove(task);
      if (!taskDao.deleteTask(task.getId()).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during task deleting");
      }
      if (!taskGroupDao.editTaskGroup(taskGroup).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during task group updating");
      }
      return ResponseEntity.ok(taskGroup.getTaskList().stream().map(GetTaskDto::new).collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

}
