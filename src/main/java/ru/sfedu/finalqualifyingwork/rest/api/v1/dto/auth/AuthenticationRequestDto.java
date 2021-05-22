package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AuthenticationRequestDto {
  @ApiModelProperty(example = "mail@example.com")
  private String email;
  @ApiModelProperty(example = "examplePassword")
  private String password;
}
