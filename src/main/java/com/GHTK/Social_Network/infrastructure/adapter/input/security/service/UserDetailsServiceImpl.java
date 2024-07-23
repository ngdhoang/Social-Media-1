package com.GHTK.Social_Network.infrastructure.adapter.input.security.service;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
//  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userGmail) throws UsernameNotFoundException {
    UserEntity userEntity = new UserEntity();
//    UserEntity userEntity = userRepository.findByUserEmail(userGmail)
//            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userGmail));
    return new UserDetailsImpl(userEntity);
  }
}
