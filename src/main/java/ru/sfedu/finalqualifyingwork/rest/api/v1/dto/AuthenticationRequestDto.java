package ru.sfedu.finalqualifyingwork.rest.api.v1.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthenticationRequestDto {
  @ApiModelProperty(example = "mail@example.com")
  private String email;
  @ApiModelProperty(example = "examplePassword")
  private String password;
}
