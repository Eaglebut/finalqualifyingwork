package ru.sfedu.finalqualifyingwork.rest.api.v1;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;
import ru.sfedu.finalqualifyingwork.repository.interfaces.GroupDao;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.GroupUserDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.PublicUserDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/group")
@AllArgsConstructor
public class GroupUserController {

  private final UserDao userDao;
  private final GroupDao groupDao;
  private final JwtTokenProvider jwtTokenProvider;


  @GetMapping(path = "{id}/users")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> getGroupUsers(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                         @RequestParam long id) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> new NoSuchElementException("user not founded"));
      var group = groupDao.getGroup(id).orElseThrow(() -> new NoSuchElementException("group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw new IllegalAccessException("User is not a part of this group");
      }
      Map<PublicUserDto, UserRole> publicUserMap = new HashMap<>();
      group.getMemberList().forEach((groupUser, userRole) -> publicUserMap.put(new PublicUserDto(groupUser), userRole));
      return ResponseEntity.ok(publicUserMap);
    } catch (NoSuchElementException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(path = "{groupId}/user/{userId}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> getGroupUser(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                        @RequestParam(name = "groupId") long groupId,
                                        @RequestParam(name = "userId") long userId) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> new NoSuchElementException("User not founded"));
      var group = groupDao.getGroup(groupId).orElseThrow(() -> new NoSuchElementException("Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw new IllegalAccessException("User is not a part of this group");
      }
      User requestUser = userDao.getUser(userId).orElseThrow(() ->
              new NoSuchElementException("User you were looking for was not found"));
      if (!group.getMemberList().containsKey(requestUser)) {
        throw new IllegalAccessException("User you were looking for is not a part of this group");
      }
      return ResponseEntity.ok(new GroupUserDto(new PublicUserDto(requestUser),
              group.getMemberList().get(requestUser)));
    } catch (NoSuchElementException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(path = "{groupId}/user/{userId}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> addUserToGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                          @RequestParam(name = "groupId") long groupId,
                                          @RequestParam(name = "userId") long userId) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> new NoSuchElementException("User not founded"));
      var group = groupDao.getGroup(groupId).orElseThrow(() -> new NoSuchElementException("Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw new IllegalAccessException("User is not a part of this group");
      }
      if (group.getMemberList().get(user).equals(UserRole.MEMBER)) {
        throw new IllegalAccessException("User does not have enough rights to create a subgroup");
      }
      User requestUser = userDao.getUser(userId).orElseThrow(() ->
              new NoSuchElementException("User you were looking for was not found"));
      if (group.getMemberList().containsKey(requestUser)) {
        throw new IllegalAccessException("User is already part of the group");
      }
      group.getMemberList().put(requestUser, UserRole.INVITED);
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw new UnexpectedException("Ошибка при обновлении информации о группе");
      }
      return ResponseEntity.ok(new GroupUserDto(new PublicUserDto(requestUser),
              group.getMemberList().get(requestUser)));
    } catch (NoSuchElementException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    } catch (UnexpectedException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (RuntimeException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping(path = "{groupId}/user/{userId}/{role}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> changeUsersRole(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                           @RequestParam(name = "groupId") long groupId,
                                           @RequestParam(name = "userId") long userId,
                                           @RequestParam(name = "role") UserRole role) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> new NoSuchElementException("User not founded"));
      var group = groupDao.getGroup(groupId).orElseThrow(() ->
              new NoSuchElementException("Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw new IllegalAccessException("User is not a part of this group");
      }
      if (group.getMemberList().get(user).equals(UserRole.MEMBER) ||
              group.getMemberList().get(user).equals(UserRole.INVITED)) {
        throw new IllegalAccessException("User does not have enough rights to change users roles");
      }
      User requestUser = userDao.getUser(userId).orElseThrow(() ->
              new NoSuchElementException("User you were looking for was not found"));
      if (role.equals(UserRole.CREATOR) || role.equals(UserRole.INVITED)) {
        throw new RuntimeException("You cant change user status to " + role.toString().toLowerCase());
      }
      group.getMemberList().replace(requestUser, role);
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw new UnexpectedException("Ошибка при обновлении информации о группе");
      }
      return ResponseEntity.ok(new GroupUserDto(new PublicUserDto(requestUser),
              group.getMemberList().get(requestUser)));
    } catch (NoSuchElementException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    } catch (UnexpectedException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (RuntimeException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(path = "{groupId}/user/{userId}")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> deleteUserFromGroup(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                               @RequestParam(name = "groupId") long groupId,
                                               @RequestParam(name = "userId") long userId) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> new NoSuchElementException("User not founded"));
      var group = groupDao.getGroup(groupId).orElseThrow(() -> new NoSuchElementException("Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw new IllegalAccessException("User is not a part of this group");
      }
      if (group.getMemberList().get(user).equals(UserRole.MEMBER)) {
        throw new IllegalAccessException("User does not have enough rights to create a subgroup");
      }
      User requestUser = userDao.getUser(userId).orElseThrow(() ->
              new NoSuchElementException("User you were looking for was not found"));
      group.getMemberList().remove(requestUser);
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw new UnexpectedException("Ошибка при обновлении информации о группе");
      }
      Map<PublicUserDto, UserRole> publicUserMap = new HashMap<>();
      group.getMemberList().forEach((groupUser, userRole) -> publicUserMap.put(new PublicUserDto(groupUser), userRole));
      return ResponseEntity.ok(publicUserMap);
    } catch (NoSuchElementException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    } catch (UnexpectedException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(path = "{groupId}/user/accept")
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> acceptInvitation(@RequestHeader("Authorization") @ApiParam(hidden = true) String token,
                                            @RequestParam(name = "groupId") long groupId) {
    try {
      var user = userDao.getUser(jwtTokenProvider.getUsername(token))
              .orElseThrow(() -> new NoSuchElementException("User not founded"));
      var group = groupDao.getGroup(groupId).orElseThrow(() ->
              new NoSuchElementException("Group not founded"));
      if (!group.getMemberList().containsKey(user)) {
        throw new IllegalAccessException("User is not a part of this group");
      }
      if (!group.getMemberList().get(user).equals(UserRole.INVITED)) {
        throw new RuntimeException("User is already accept invitation of the group");
      }
      group.getMemberList().replace(user, UserRole.MEMBER);
      if (!groupDao.editGroup(group).equals(Statuses.SUCCESS)) {
        throw new UnexpectedException("Ошибка при обновлении информации о группе");
      }
      return ResponseEntity.ok(new GroupUserDto(new PublicUserDto(user),
              group.getMemberList().get(user)));
    } catch (NoSuchElementException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    } catch (UnexpectedException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (RuntimeException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }


}
