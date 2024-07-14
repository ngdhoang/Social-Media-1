package com.GHTK.Social_Network.application.service.cloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudService {
  private final Cloudinary cloudinary;

  public boolean isBase64(String input) {
    try {
      String base64String = input.split(",")[1].trim();

      byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64String);
      String reencodedString = java.util.Base64.getEncoder().encodeToString(decodedBytes);

      return reencodedString.equals(base64String);
    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
      return false;
    }
  }

  public String uploadPictureByBase64(String base64String) {
    try {
      byte[] decodedBytes = Base64.decodeBase64(base64String.split(",")[1].trim());
      MultipartFile multipartFile = new BASE64DecodedMultipartFile(decodedBytes);
      return uploadPictureByFile(multipartFile);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decode base64 string or create MultipartFile", e);
    }
  }

  public String uploadPictureByFile(MultipartFile file) {
    try {
      Map data = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
      if (data.containsKey("url")) {
        return (String) data.get("url");
      } else {
        throw new RuntimeException("Failed to upload image to Cloudinary. Response: " + data.toString());
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file bytes or upload to Cloudinary", e);
    }
  }
}
