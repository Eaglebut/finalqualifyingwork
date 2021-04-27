package ru.sfedu.finalqualifyingwork.rest.api.v1;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.GetUserDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.PostUserDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

@RestController
@RequestMapping("/v1/user")
@AllArgsConstructor
public class UserController {

  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;


  @GetMapping
  @PreAuthorize("hasAuthority('user:all')")

  public ResponseEntity<GetUserDto> getUser(@RequestHeader("Authorization") @ApiParam(hidden = true) String token) {
    var optUser = userDao.getUser(jwtTokenProvider.getUsername(token));
    return optUser.map(user -> ResponseEntity.ok(new GetUserDto(user)))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  @PutMapping
  @PreAuthorize("hasAuthority('user:all')")
  public ResponseEntity<?> updateUser(@RequestBody PostUserDto postUserDto) {
    var user = postUserDto.toUser();
    var serverUser = userDao.getUser(user.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not founded"));
    user.setId(serverUser.getId());
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userDao.editUser(user).equals(Statuses.SUCCESS)
            ? ResponseEntity.ok(new GetUserDto(user))
            : new ResponseEntity<>("Invalid user", HttpStatus.BAD_REQUEST);
  }

}
