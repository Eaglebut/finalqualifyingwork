package ru.sfedu.finalqualifyingwork.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {
  USER_ALL("user:all");
  private final String permission;
}
