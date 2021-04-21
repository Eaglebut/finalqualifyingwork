package ru.sfedu.finalqualifyingwork.model.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {
  DEVELOPERS_READ("developers:read"),
  DEVELOPERS_WRITE("developers:write");

  private final String permission;
}
