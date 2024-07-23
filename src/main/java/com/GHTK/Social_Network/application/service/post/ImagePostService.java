package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.AsyncImageUploadPortInput;
import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ImagePostMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.UpdateImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagePostService implements ImagePostInput {
  private final AsyncImageUploadPortInput asyncImageUploadPortInputs;
  private final ImageHandlerPortInput imageHandlerPortInput;
  private final RedisImageTemplatePort redisImageTemplatePort;
  private final ImagePostPort imagePostPort;
  private final AuthPort authPort;
  private final PostPort postPort;
  private final CloudPort cloudPort;

  private String generateRandomPublicId(int n) {
    String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    SecureRandom RANDOM = new SecureRandom();

    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      int index = RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }
    return sb.toString();
  }

  @Override
  public ImagePostDto createImage(CreateImageRequest request) {
    imageHandlerPortInput.checkImageValid(request.getFile());
    String publicId = this.generateRandomPublicId(8);

    asyncImageUploadPortInputs.uploadImageAsync(request.getFile(), publicId + "_" + authPort.getUserAuth().getUserEmail());

    return new ImagePostDto(publicId, "");
  }

//  public ImageResponse createImage(CreateImageRequest request) {
//    cloudServicePortInput.checkImageValid(request.getFile());
//    Map data = cloudServicePortInput.uploadPictureByFile(request.getFile(), ImageHandlerPortInput.MAX_SIZE_POST);
//    ImageResponse imageResponse = new ImageResponse();
//    ImagePostDto imageDto = new ImagePostDto(cloudServicePortInput.extractPublicId(data), cloudServicePortInput.extractUrl(data));
//    imageRedisTemplate.opsForValue().set(cloudServicePortInput.extractPublicId(data), cloudServicePortInput.extractUrl(data));
//    List<ImagePostDto> imageDtoList = new ArrayList<>();
//    imageDtoList.add(imageDto);
//    imageResponse.setImageDtoList(imageDtoList);
//    return imageResponse;
//  }

  @Override
  public ImagePostDto updateImage(UpdateImagePostDto request) {
    deleteImage(request.getImageId());
    return createImage(new CreateImageRequest(request.getImage()));
  }

  @Override
  public MessageResponse deleteImage(Long id) {
    ImagePost imagePost = imagePostPort.findImageById(id);
    if (imagePost == null) {
      throw new CustomException("Image not found", HttpStatus.NOT_FOUND);
    }

    if (!authPort.getUserAuth().equals(authPort.getUserById(postPort.findPostByImagePostId(imagePost.getImagePostId()).getPostId()))) {
      throw new CustomException("User not authorized", HttpStatus.FORBIDDEN);
    }

    if (!cloudPort.deletePictureByUrl(imagePost.getImageUrl())) {
      throw new CustomException("Image cannot delete", HttpStatus.FORBIDDEN);
    }

    imagePostPort.deleteImageById(id);
    return new MessageResponse("Image deleted successfully");
  }

  @Override
  public MessageResponse deleteImageInRedis(String publicId) {
    if (Boolean.TRUE.equals(redisImageTemplatePort.existsByKey(publicId))) {
      redisImageTemplatePort.deleteByKey(publicId);
      return new MessageResponse("Image deleted successfully");
    }
    throw new CustomException("Image not found", HttpStatus.NOT_FOUND);
  }

  @Override
  public ImageResponse getImageById(Long id) {
    return null;
  }

  @Override
  public List<ImageDto> getImageByPostId(Long id) {
    Post post = postPort.findPostById(id);
    List<ImagePost> imagePostEntities = postPort.findAllImageByPostId(post.getPostId());
    List<ImageDto> imageResponseList = new ArrayList<>();
    imagePostEntities.forEach(imagePost -> {
      imageResponseList.add(ImagePostMapper.INSTANCE.imagePostToImageDto(imagePost));
    });
    return imageResponseList;
  }
}
