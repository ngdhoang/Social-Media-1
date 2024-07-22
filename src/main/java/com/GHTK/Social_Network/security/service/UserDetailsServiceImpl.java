package com.GHTK.Social_Network.security.service;

import com.GHTK.Social_Network.domain.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userGmail) throws UsernameNotFoundException {
    UserEntity userEntity = UserMapper.INSTANCE.toEntity(userRepository.findByUserEmail(userGmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userGmail)));
    return new UserDetailsImpl(userEntity);
  }
}
