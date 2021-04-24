package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import ru.sfedu.finalqualifyingwork.model.enums.UserRole;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity
public class Group extends BaseEntity {
  private String name;
  @ElementCollection
  private Map<User, UserRole> memberList;
  @OneToMany
  private Set<Group> subGroups;
  @OneToMany
  private List<TaskGroup> taskGroups;
}
