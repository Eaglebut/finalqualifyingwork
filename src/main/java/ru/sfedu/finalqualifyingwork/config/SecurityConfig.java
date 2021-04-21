package ru.sfedu.finalqualifyingwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.sfedu.finalqualifyingwork.model.enums.Permission;
import ru.sfedu.finalqualifyingwork.model.enums.Role;
import ru.sfedu.finalqualifyingwork.repository.HibernateUserDao;
import ru.sfedu.finalqualifyingwork.repository.UserDao;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
    ;
  }

  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(
            User.builder().username("admin")
                    // Use without encode first
                    .password(passwordEncoder().encode("admin"))
                    .authorities(Role.ADMIN.getAuthorities())
                    .build(),
            User.builder().username("user")
                    .password(passwordEncoder().encode("user"))
                    .authorities(Role.USER.getAuthorities())
                    .build()

    );
    // Go to UserDetailsServiceImpl - InMemory
  }

  @Bean
  protected PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  protected UserDao userDao(){
    return new HibernateUserDao();
  }
}