package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.AsyncImageUploadPortInput;
import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImagePostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagePostService implements ImagePostInput {
  private static final Logger log = LoggerFactory.getLogger(ImagePostService.class);
  private final AsyncImageUploadPortInput asyncImageUploadPortInputs;
  private final ImageHandlerPortInput imageHandlerPortInput;

  private final RedisImageTemplatePort redisImageTemplatePort;
  private final ImagePostPort imagePostPort;
  private final AuthPort authPort;
  private final CloudPort cloudPort;

  @Override
  public ImagePostDto createImage(MultipartFile imageFile, String tail) {
    imageHandlerPortInput.checkImageValid(imageFile);
    String publicId = generateRandomPublicId();
    String key = publicId + "_" + tail + "_" + authPort.getUserAuth().getUserEmail();
    redisImageTemplatePort.createOrUpdate(key, ImagePostInput.VALUE_LOADING); // Set image loading

    asyncImageUploadPortInputs.uploadImageAsync(imageFile, key);
    return new ImagePostDto(publicId, "");
  }

  @Override
  public void deleteImagePost(List<ImagePost> imagePosts) {
    imagePosts.forEach(imagePost -> {
      if (!cloudPort.deletePictureByUrl(imagePost.getImageUrl())) {
        log.error("Image cannot delete");
      }

      imagePostPort.deleteImageById(imagePost.getImagePostId());
    });
  }

  private String generateRandomPublicId() {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    return uuid.substring(0, Math.min(uuid.length(), ImagePostInput.MAX_LENGTH_GENERATE));
  }
}
