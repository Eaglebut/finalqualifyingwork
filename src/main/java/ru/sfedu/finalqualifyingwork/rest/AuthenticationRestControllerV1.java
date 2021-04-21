package ru.sfedu.finalqualifyingwork.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.repository.UserDao;
import ru.sfedu.finalqualifyingwork.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

  private final AuthenticationManager authenticationManager;
  private final UserDao userDao;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, UserDao userDao, JwtTokenProvider jwtTokenProvider) {
    this.authenticationManager = authenticationManager;
    this.userDao = userDao;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto requestDto) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
              requestDto.getPassword()));
      User user = userDao.getUser(requestDto.getEmail())
              .orElseThrow(() -> new UsernameNotFoundException("User not founded"));
      String token = jwtTokenProvider.createToken(requestDto.getEmail(), user.getRole().name());
      Map<Object, Object> response = new HashMap<>();
      response.put("email", requestDto.getEmail());
      response.put("token", token);
      return ResponseEntity.ok(response);
    }catch (AuthenticationException e){
      return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    var securityContextLogoutHandler = new SecurityContextLogoutHandler();
    securityContextLogoutHandler.logout(request, response, null);
  }
}
