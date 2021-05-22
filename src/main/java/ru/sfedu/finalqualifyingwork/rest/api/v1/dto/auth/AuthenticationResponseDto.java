package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.auth;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class AuthenticationResponseDto {
  private String email;
  private String token;
}
