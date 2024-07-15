package com.GHTK.Social_Network.application.service.cloud;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.ImageHandlerPort;
import com.GHTK.Social_Network.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageHandlerService implements ImageHandlerPortInput {
  private final CloudService cloudService;

  private final ImageHandlerPort imageHandlerPort;

  private final AuthPort authenticationRepositoryPort;

  private User getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  @Override
  public String uploadImageToCloud(String base64) {
    if (cloudService.isBase64(base64)) {
      return cloudService.uploadPictureByBase64(base64);
    }
    return null;
  }

  @Override
  public String uploadImagePost(String base64) {

    return null;
  }

  @Override
  public String uploadImageComment(String base64) {
    return null;
  }

  @Override
  public String uploadImageUser(String base64) {
    String url = uploadImageToCloud(base64);
    if (imageHandlerPort.saveAvatar(base64, getUserAuth().getUserId())) {
      return url;
    }
    return null;
  }
}
