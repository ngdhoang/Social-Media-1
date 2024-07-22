package com.GHTK.Social_Network.security.service;

import com.GHTK.Social_Network.common.customException.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationManager {

  private final UserDetailsService userDetailsService;

  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (userDetails != null) {
      if (passwordEncoder.matches(password, userDetails.getPassword())) {
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
      } else {
        throw new CustomException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
      }
    } else {
      throw new CustomException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
    }
  }
}