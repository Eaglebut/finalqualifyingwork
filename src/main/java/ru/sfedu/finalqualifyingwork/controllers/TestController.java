package ru.sfedu.finalqualifyingwork.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.finalqualifyingwork.data.DataProvider;
import ru.sfedu.finalqualifyingwork.model.User;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping
  public List<User> getUserList() {
    DataProvider dataProvider = new DataProvider();
    return dataProvider.getEntities(User.class);
  }
}
