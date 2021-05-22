package ru.sfedu.finalqualifyingwork.rest.api.v1;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.auth.AuthenticationRequestDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.auth.AuthenticationResponseDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.GetUserDto;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.PostUserDto;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationRestController {

  private final AuthenticationManager authenticationManager;
  private final UserDao userDao;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto requestDto) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
              requestDto.getPassword()));
      User user = userDao.getUser(requestDto.getEmail())
              .orElseThrow(() -> new UsernameNotFoundException("User not founded"));
      String token = jwtTokenProvider.createToken(requestDto.getEmail(), user.getRole().name());
      var response = new AuthenticationResponseDto();
      response.setEmail(requestDto.getEmail());
      response.setToken(token);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AuthenticationException e) {
      return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    var securityContextLogoutHandler = new SecurityContextLogoutHandler();
    securityContextLogoutHandler.logout(request, response, null);
  }

  @PostMapping("/register")
  public ResponseEntity<?> createUser(@RequestBody PostUserDto userDto) {
    var user = userDto.toUser();
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    return userDao.saveUser(user).equals(Statuses.SUCCESS)
            ? ResponseEntity.ok(new GetUserDto(user))
            : new ResponseEntity<>("Invalid user", HttpStatus.BAD_REQUEST);
  }
}
