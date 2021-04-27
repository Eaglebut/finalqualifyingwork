package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@Entity
@Data
@ToString(callSuper = true)
public class TaskGroup extends BaseEntity {
  private String name;
  @OneToMany(fetch = FetchType.EAGER)
  private List<Task> taskList;
}
