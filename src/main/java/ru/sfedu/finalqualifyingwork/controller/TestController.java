package ru.sfedu.finalqualifyingwork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.repository.UserDao;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

  private final UserDao userDao;

  @Autowired
  public TestController(UserDao userDao) {
    this.userDao = userDao;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('developers:read')")
  public List<User> getUserList() {
    return userDao.getUserList();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('developers:read')")
  public ResponseEntity<User> getUser(@PathVariable long id){
     var optUser = userDao.getUser(id);
     return optUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('developers:write')")
  public User createUser(@RequestBody User user){
    userDao.saveUser(user);
    return user;
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('developers:write')")
  public void deleteUser(@PathVariable long id){
    userDao.deleteUser(id);
  }
}
