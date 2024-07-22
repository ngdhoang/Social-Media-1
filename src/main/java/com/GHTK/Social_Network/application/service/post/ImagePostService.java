package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.CloudServicePortInput;
import com.GHTK.Social_Network.application.port.input.RandomStringGeneratorPortInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.domain.entity.post.ImagePost;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ImagePostMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.UpdateImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagePostService implements ImagePostInput {
  private final CloudServicePortInput cloudServicePortInput;

  private final RedisTemplate<String, String> imageRedisTemplate;

  private final ImagePostPort imagePostPort;

  private final AuthPort authenticationRepositoryPort;

  private final PostPort postPort;

  private final RandomStringGeneratorPortInput randomStringGeneratorPortInput;

  private final AsyncImageUpload asyncImageUpload;

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
  public ImagePostDto createImage(CreateImageRequest request) {
    cloudServicePortInput.checkImageValid(request.getFile());
    String publicId = randomStringGeneratorPortInput.generateRandomPublicId(8);

    asyncImageUpload.uploadImageAsync(request.getFile(), publicId + "_" + getUserAuth().getUserEmail());

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
//
//
//
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

    if (!getUserAuth().equals(postPort.findUserById(postPort.findPostByImagePost(imagePost).getPostId()))) {
      throw new CustomException("User not authorized", HttpStatus.FORBIDDEN);
    }

    if (!cloudServicePortInput.deletePictureByUrl(imagePost.getImageUrl())) {
      throw new CustomException("Image cannot delete", HttpStatus.FORBIDDEN);
    }

    imagePostPort.deleteImageById(id);
    return new MessageResponse("Image deleted successfully");
  }

  @Override
  public MessageResponse deleteImageInRedis(String public_id) {
    if (Boolean.TRUE.equals(imageRedisTemplate.hasKey(public_id))) {
      imageRedisTemplate.delete(public_id);
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
    List<ImagePost> imagePosts = post.getImagePosts();
    List<ImageDto> imageResponseList = new ArrayList<>();
    imagePosts.forEach(imagePost -> {
      imageResponseList.add(ImagePostMapper.INSTANCE.imagePostToImageDto(imagePost));
    });
    return imageResponseList;
  }
}
