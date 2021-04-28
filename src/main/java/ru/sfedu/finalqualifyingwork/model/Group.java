package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.finalqualifyingwork.model.enums.GroupType;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;

import javax.persistence.*;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity(name = "groups")
public class Group extends BaseEntity {
  private String name;
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(name = "user_role")
  @Enumerated(EnumType.STRING)
  @MapKeyJoinColumn(name = "user_id")
  private Map<User, UserRole> memberList = new HashMap<>();
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "baseGroup", cascade = CascadeType.ALL)
  private Set<Group> subGroups = new HashSet<>();
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  private List<TaskGroup> taskGroups = new ArrayList<>();
  private GroupType groupType;
  @ManyToOne(fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Group baseGroup;
}
