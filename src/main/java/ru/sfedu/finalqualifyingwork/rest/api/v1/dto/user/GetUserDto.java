package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.User;

@Data
@NoArgsConstructor
public class GetUserDto {
  private long id;
  @ApiModelProperty(example = "mail@example.com")
  private String email;
  @ApiModelProperty(example = "John")
  private String name;
  @ApiModelProperty(example = "Doe")
  private String surname;

  public GetUserDto(User user) {
    id = user.getId();
    email = user.getEmail();
    name = user.getName();
    surname = user.getSurname();
  }

  public User toUser() {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    user.setName(name);
    user.setSurname(surname);
    return user;
  }
}
