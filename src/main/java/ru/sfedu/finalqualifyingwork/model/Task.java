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
public class Task extends BaseEntity {
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String text;
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private User author;
  @EqualsAndHashCode.Exclude
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL)
  private List<Comment> commentList = new ArrayList<>();
  private int position;
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private TaskGroup owner;
}
