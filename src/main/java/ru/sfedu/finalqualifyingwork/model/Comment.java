package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@Data
@Entity
@ToString(callSuper = true)
public class Comment extends BaseEntity{
  @ManyToOne(fetch = FetchType.EAGER)
  private User author;
  @OneToMany(fetch = FetchType.EAGER)
  private List<Comment> commentList;
  private String text;
}
