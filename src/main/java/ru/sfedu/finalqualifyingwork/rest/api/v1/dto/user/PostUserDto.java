package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.model.enums.Role;

@Data
@NoArgsConstructor
@ApiModel
public class PostUserDto {

  @ApiModelProperty(example = "mail@example.com")
  private String email;
  @ApiModelProperty(example = "examplePassword")
  private String password;
  @ApiModelProperty(example = "John")
  private String name;
  @ApiModelProperty(example = "Doe")
  private String surname;

  public PostUserDto(User user) {
    email = user.getEmail();
    name = user.getName();
    surname = user.getSurname();
    password = user.getPassword();
  }

  public User toUser() {
    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setSurname(surname);
    user.setPassword(password);
    user.setRole(Role.USER);
    user.setStatus(AccountStatus.ACTIVE);
    return user;
  }
}