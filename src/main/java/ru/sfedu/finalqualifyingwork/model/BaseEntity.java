package ru.sfedu.finalqualifyingwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
@ToString
@EqualsAndHashCode(doNotUseGetters = true)
public class BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(nullable = false)
  private Date created = new Date();
  @Column(nullable = false)
  private Date lastUpdated = new Date();
}
