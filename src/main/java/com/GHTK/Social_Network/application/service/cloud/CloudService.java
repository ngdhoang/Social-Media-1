package com.GHTK.Social_Network.application.service.cloud;

import com.GHTK.Social_Network.application.port.input.CloudServicePortInput;
import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudService implements CloudServicePortInput {
  private final Cloudinary cloudinary;

  private final ImageHandlerPortInput imageHandlerPortInput;

  @Override
  public String uploadPictureByBase64(String base64String) {
    try {
      if (!imageHandlerPortInput.isBase64(base64String)) {
        throw new CustomException("Input not base64", HttpStatus.BAD_REQUEST);
      }
      MultipartFile multipartFile = imageHandlerPortInput.convertBase64ToMultipartFile(base64String);
      return uploadPictureByFile(multipartFile);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decode base64 string or create MultipartFile", e);
    }
  }

  @Override
  public String uploadPictureByFile(MultipartFile file) {
    try {
      Map data = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
      if (data.containsKey("url")) {
        return (String) data.get("url");
      } else {
        throw new RuntimeException("Failed to upload image to Cloudinary. Response: " + data);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file bytes or upload to Cloudinary", e);
    }
  }

  @Override
  public String uploadPictureSetSize(String base64String, Long size) {
      if (!imageHandlerPortInput.isBase64(base64String)) {
        throw new CustomException("Input not base64", HttpStatus.BAD_REQUEST);
      }

      if (!imageHandlerPortInput.checkSizeValid(base64String, ImageHandlerPortInput.MAX_SIZE_NOT_VALID)) {
        throw new CustomException("Size not valid", HttpStatus.BAD_REQUEST);
      }

      if (!imageHandlerPortInput.isImage(base64String)) {
        throw new CustomException("Image not base64", HttpStatus.BAD_REQUEST);
      }

      MultipartFile multipartFile = imageHandlerPortInput.compressImage(base64String, size);
      return uploadPictureByFile(multipartFile);
  }

  private String extractPublicIdFromUrl(String imageUrl) {
    String withoutExtension = imageUrl.substring(0, imageUrl.lastIndexOf('.'));

    return withoutExtension.substring(withoutExtension.lastIndexOf('/') + 1);
  }

  @Override
  public boolean deletePictureByUrl(String url) {
    try {
      // Extract public ID from URL
      String publicId = extractPublicIdFromUrl(url);

      // Delete the image
      Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

      // Check if deletion was successful
      return "ok".equals(result.get("result"));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
