package ru.sfedu.finalqualifyingwork.model;

import lombok.*;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.model.enums.Role;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "\"user\"")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String email;
  @Column(nullable = false)
  private String password;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String surname;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AccountStatus status;
}
