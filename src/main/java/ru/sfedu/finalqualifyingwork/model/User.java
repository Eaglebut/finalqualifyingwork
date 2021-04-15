package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String password;
}
