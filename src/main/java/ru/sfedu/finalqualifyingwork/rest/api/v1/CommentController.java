package ru.sfedu.finalqualifyingwork.rest.api.v1;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.sfedu.finalqualifyingwork.model.*;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.repository.interfaces.*;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.comment.GetCommentDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.comment.PostCommentDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.comment.PutCommentDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/group/{groupId}/taskGroup/{taskGroupId}/task/{taskId}/")
@AllArgsConstructor
@PreAuthorize("hasAuthority('user:all')")
@CrossOrigin(origins = "*")
public class CommentController {
  private final UserDao userDao;
  private final GroupDao groupDao;
  private final JwtTokenProvider jwtTokenProvider;
  private final TaskGroupDao taskGroupDao;
  private final TaskDao taskDao;
  private final CommentDao commentDao;

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

  private Comment getComment(long commentId, Task task) {
    var comment = commentDao.getComment(commentId).orElseThrow(() ->
            createHttpException(HttpStatus.NOT_FOUND, "Comment not founded"));
    if (!checkBaseComment(comment, task)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "Comment is not a part of this task");
    }
    return comment;
  }

  private boolean checkBaseComment(Comment comment, Task task) {
    if (comment.getBaseComment() == null)
      return task.getCommentList().contains(comment);
    return checkBaseComment(comment.getBaseComment(), task);
  }

  private void checkUserPermissions(User user, Group group) throws HttpClientErrorException {
    if (group.getMemberList().get(user).equals(UserRole.INVITED)) {
      throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to create task");
    }
  }


  @GetMapping("comments")
  public ResponseEntity<?> getTasksComments(@RequestHeader("Authorization")
                                            @ApiParam(hidden = true) String token,
                                            @PathVariable long groupId,
                                            @PathVariable long taskGroupId,
                                            @PathVariable long taskId) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var task = getTask(taskId, taskGroup);
      return ResponseEntity.ok(task.getCommentList()
              .stream()
              .map(GetCommentDto::new)
              .collect(Collectors.toList()));
    } catch (HttpClientErrorException.BadRequest exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PostMapping("comment")
  public ResponseEntity<?> createTask(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                      @PathVariable long groupId,
                                      @PathVariable long taskGroupId,
                                      @PathVariable long taskId,
                                      @RequestBody PostCommentDto commentDto) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var task = getTask(taskId, taskGroup);
      Comment comment = commentDto.toComment();
      comment.setAuthor(user);
      comment.setOwner(task);
      if (commentDto.getBaseCommentId() != 0) {
        comment.setBaseComment(getComment(commentDto.getBaseCommentId(), task));
        comment.setOwner(null);
      }
      if (!commentDao.createComment(comment).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.BAD_REQUEST, "Invalid comment");
      }
      group = getGroup(groupId, user);
      taskGroup = getTaskGroup(taskGroupId, group);
      task = getTask(taskId, taskGroup);
      return ResponseEntity.ok(task.getCommentList()
              .stream()
              .map(GetCommentDto::new)
              .collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PutMapping("comment/{commentId}")
  public ResponseEntity<?> editTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                         @PathVariable long groupId,
                                         @PathVariable long taskGroupId,
                                         @PathVariable long taskId,
                                         @PathVariable long commentId,
                                         @RequestBody PutCommentDto commentDto) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var task = getTask(taskId, taskGroup);
      var comment = getComment(commentId, task);
      if (!comment.getText().equals(commentDto.getText())) {
        comment.setText(commentDto.getText());
        if (!commentDao.editComment(comment).equals(Statuses.SUCCESS)) {
          throw createHttpException(HttpStatus.BAD_REQUEST, "Invalid comment");
        }
        group = getGroup(groupId, user);
        taskGroup = getTaskGroup(taskGroupId, group);
        task = getTask(taskId, taskGroup);
      }

      return ResponseEntity.ok(task.getCommentList()
              .stream()
              .map(GetCommentDto::new)
              .collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @DeleteMapping("comment/{commentId}")
  public ResponseEntity<?> deleteTaskGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                           @PathVariable long groupId,
                                           @PathVariable long taskGroupId,
                                           @PathVariable long taskId,
                                           @PathVariable long commentId) {
    try {
      var user = getUser(token);
      var group = getGroup(groupId, user);
      checkUserPermissions(user, group);
      var taskGroup = getTaskGroup(taskGroupId, group);
      var task = getTask(taskId, taskGroup);
      var comment = getComment(commentId, task);
      if (!commentDao.deleteComment(comment.getId()).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during comment deleting");
      }
      group = getGroup(groupId, user);
      taskGroup = getTaskGroup(taskGroupId, group);
      task = getTask(taskId, taskGroup);
      return ResponseEntity.ok(task.getCommentList()
              .stream()
              .map(GetCommentDto::new)
              .collect(Collectors.toList()));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }
}
