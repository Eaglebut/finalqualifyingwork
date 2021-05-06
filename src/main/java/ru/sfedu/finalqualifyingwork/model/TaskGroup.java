package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@Entity
@Data
@ToString(callSuper = true)
public class TaskGroup extends BaseEntity {
  @Column(nullable = false)
  private String name;
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "owner")
  @EqualsAndHashCode.Exclude
  @OrderBy("position")
  private List<Task> taskList = new ArrayList<>();
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Group ownerGroup;
  @Column(nullable = false)
  private int position;
}
