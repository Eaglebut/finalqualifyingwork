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
  @Column(unique = true)
  private String email;
  private String password;
  private String name;
  private String surname;
  @Enumerated(EnumType.STRING)
  private Role role;
  @Enumerated(EnumType.STRING)
  private AccountStatus status;
}
