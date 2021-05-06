package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@Entity
@Data
@ToString(callSuper = true)
public class TaskGroup extends BaseEntity {
  private String name;
  @OneToMany(fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  private List<Task> taskList = new ArrayList<>();
  @ManyToOne(fetch = FetchType.EAGER)
  @ToString.Exclude
  private Group ownerGroup;
  private int position;
}
