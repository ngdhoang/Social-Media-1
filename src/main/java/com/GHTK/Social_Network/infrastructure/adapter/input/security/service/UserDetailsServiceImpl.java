package com.GHTK.Social_Network.infrastructure.adapter.input.security.service;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.AuthRepositoryPortImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final AuthRepositoryPortImpl userRepository;

  @Override
  public UserDetails loadUserByUsername(String userGmail) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(userGmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userGmail));
    return new UserDetailsImpl(user);
  }
}
