package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ImageHandlerPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageHandlerPortImpl implements ImageHandlerPort {
  private final UserRepository userRepository;

  @Override
  public Boolean saveAvatar(String avatar, Long id) {
    int check = userRepository.changeAvatar(avatar, id);
    return check != 0;
  }
}
