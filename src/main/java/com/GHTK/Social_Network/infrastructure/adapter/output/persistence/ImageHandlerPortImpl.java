package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ImageHandlerPort;
import com.GHTK.Social_Network.infrastructure.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageHandlerPortImpl implements ImageHandlerPort {
  private final UserEntityRepository userEntityRepository;

  @Override
  public Boolean saveAvatar(String avatar, Long id) {
    int check = userEntityRepository.changeAvatar(avatar, id);
    return check != 0;
  }
}
