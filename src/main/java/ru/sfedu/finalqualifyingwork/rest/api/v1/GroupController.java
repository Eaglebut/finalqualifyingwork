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
import ru.sfedu.finalqualifyingwork.model.enums.GroupType;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.group.GetGroupDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.group.PostGroupDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/group")
@AllArgsConstructor
@PreAuthorize("hasAuthority('user:all')")
@CrossOrigin(origins = "*")
public class GroupController {

  private final UserDao userDao;
  private final GroupDao groupDao;
  private final JwtTokenProvider jwtTokenProvider;


  private HttpClientErrorException createHttpException(HttpStatus status, String message) {
    return HttpClientErrorException.create(status, message, HttpHeaders.EMPTY, null, null);
  }

  @GetMapping()
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> getUsersGroups(@RequestHeader("Authorization") @ApiParam(hidden = true) String token) {
    try {
      return new ResponseEntity<>(groupDao.getGroup(userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "user not founded")))
              .stream()
              .filter(group -> !group.getGroupType().equals(GroupType.SUBGROUP))
              .map(GetGroupDto::new)
              .collect(Collectors.toList()),
              HttpStatus.OK);
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @GetMapping(path = "{id}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> getGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                    @PathVariable long id) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "user not founded"));
      var group = groupDao.getGroup(id).orElseThrow(() ->
              createHttpException(HttpStatus.NOT_FOUND, "group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw createHttpException(HttpStatus.FORBIDDEN, "User is not a part of this group");
      }
      return new ResponseEntity<>(new GetGroupDto(group), HttpStatus.OK);
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PostMapping
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> createGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                       @RequestBody PostGroupDto groupDto) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "user not founded"));
      Group group = new Group();
      group.setName(groupDto.getName());
      group.setDescription(groupDto.getDescription());
      group.getMemberList().put(user, UserRole.CREATOR);
      group.setGroupType(GroupType.STANDARD_GROUP);
      if (groupDto.getBaseGroupId() != 0) {
        group.setGroupType(GroupType.SUBGROUP);
        var baseGroup = groupDao.getGroup(groupDto.getBaseGroupId())
                .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "Group not founded"));
        if (!baseGroup.getMemberList().containsKey(user)) {
          throw createHttpException(HttpStatus.FORBIDDEN, "User is not a part of a base group");
        }
        if (baseGroup.getMemberList().get(user).equals(UserRole.MEMBER)) {
          throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to create a subgroup");
        }
        group.setBaseGroup(baseGroup);
        if (!groupDao.createGroup(group).equals(Statuses.SUCCESS)) {
          throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Something goes wrong");
        }
        return ResponseEntity.ok(new GetGroupDto(group));
      }
      if (!groupDao.createGroup(group).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Something goes wrong");
      }
      return ResponseEntity.ok(new GetGroupDto(group));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @PutMapping(path = "{id}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> updateGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                       @RequestBody PostGroupDto groupDto,
                                       @PathVariable long id) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "user not founded"));
      var group = groupDao.getGroup(id).orElseThrow(() ->
              createHttpException(HttpStatus.NOT_FOUND, "Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw createHttpException(HttpStatus.FORBIDDEN, "User is not a part of group");
      }
      if (group.getMemberList().get(user).equals(UserRole.MEMBER)) {
        throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to edit group");
      }
      if ((group.getBaseGroup() == null && groupDto.getBaseGroupId() != 0)
              || groupDto.getBaseGroupId() != 0 && groupDto.getBaseGroupId() != group.getBaseGroup().getId()) {
        var newBaseGroup = groupDao.getGroup(groupDto.getBaseGroupId())
                .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "Base group not founded"));
        if (!newBaseGroup.getMemberList().containsKey(user)) {
          throw createHttpException(HttpStatus.FORBIDDEN, "User is not a part of a base group");
        }
        if (newBaseGroup.getMemberList().get(user).equals(UserRole.MEMBER)) {
          throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to create a subgroup");
        }
        group.setBaseGroup(newBaseGroup);
        group.setGroupType(GroupType.SUBGROUP);
      }
      if (groupDto.getBaseGroupId() == 0) {
        group.setBaseGroup(null);
        group.setGroupType(GroupType.STANDARD_GROUP);
      }
      group.setName(groupDto.getName());
      group.setDescription(groupDto.getDescription());
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Something goes wrong");
      }
      return ResponseEntity.ok(new GetGroupDto(group));
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }

  @DeleteMapping(path = "{id}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> deleteGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                       @PathVariable long id) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> createHttpException(HttpStatus.NOT_FOUND, "user not founded"));
      var group = groupDao.getGroup(id).orElseThrow(() ->
              createHttpException(HttpStatus.NOT_FOUND, "Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw createHttpException(HttpStatus.FORBIDDEN, "User is not a part of group");
      }
      if (!group.getMemberList().get(user).equals(UserRole.CREATOR)) {
        throw createHttpException(HttpStatus.FORBIDDEN, "User does not have enough rights to edit group");
      }
      if (!groupDao.deleteGroup(id).equals(Statuses.SUCCESS)) {
        throw createHttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Something goes wrong");
      }
      return ResponseEntity.ok().build();
    } catch (HttpClientErrorException exception) {
      return new ResponseEntity<>(exception.getStatusText(), exception.getStatusCode());
    }
  }


}
