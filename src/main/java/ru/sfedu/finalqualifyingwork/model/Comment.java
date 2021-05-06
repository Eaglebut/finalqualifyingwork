package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@Data
@Entity
@ToString(callSuper = true)
public class Comment extends BaseEntity {
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private User author;
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "baseComment", cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  private List<Comment> commentList = new ArrayList<>();
  @Column(nullable = false)
  private String text;
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private Task owner;
  @ManyToOne(fetch = FetchType.EAGER)
  private Comment baseComment;
}
