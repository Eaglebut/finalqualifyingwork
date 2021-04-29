package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.enums.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserDto {
  private PublicUserDto user;
  private UserRole role;

}
