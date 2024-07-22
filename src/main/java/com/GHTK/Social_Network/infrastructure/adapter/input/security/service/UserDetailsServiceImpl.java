package com.GHTK.Social_Network.infrastructure.adapter.input.security.service;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.AuthAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final AuthAdapter userRepository;

  @Override
  public UserDetails loadUserByUsername(String userGmail) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.findByEmail(userGmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userGmail));
    return new UserDetailsImpl(userEntity);
  }
}
