package ru.sfedu.finalqualifyingwork.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.AccountStatus;
import ru.sfedu.finalqualifyingwork.repository.interfaces.UserDao;

@Service("userDetailsServiceImpl")
public class DaoUserDetailsService implements UserDetailsService {

  private final UserDao userDao;

  @Autowired
  public DaoUserDetailsService(@Qualifier("userDaoImpl") UserDao userDao) {
    this.userDao = userDao;
  }

  public static UserDetails toSpringUserDetails(User user) {
    return new org.springframework.security.core.userdetails.User(user.getEmail(),
            user.getPassword(),
            user.getStatus().equals(AccountStatus.ACTIVE),
            user.getStatus().equals(AccountStatus.ACTIVE),
            user.getStatus().equals(AccountStatus.ACTIVE),
            user.getStatus().equals(AccountStatus.ACTIVE),
            user.getRole().getAuthorities());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return toSpringUserDetails(userDao.getUser(username).orElseThrow(() ->
            new UsernameNotFoundException("User not founded")));
  }
}
