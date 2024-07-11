package com.GHTK.Social_Network.authentication.infrastructure.adapters.output.security.sevices;

import com.GHTK.Social_Network.authentication.domain.entities.user.Users;
import com.GHTK.Social_Network.authentication.infrastructure.adapters.output.persistence.UserRepository;
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
    Users user = userRepository.findByUserEmail(userGmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userGmail));
    return new UserDetailsImpl(user);
  }
}
