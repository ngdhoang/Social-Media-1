package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.common.customException.CustomException;
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
public class CloudAdapter implements CloudPort {
  private final Cloudinary cloudinary;
  private final ImageHandlerPortInput imageHandlerPortInput;

  @Override
  public Map uploadPictureByFile(MultipartFile file) {
    try {
      Map data = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
      if (data.containsKey("url")) {
        return data;
      } else {
        throw new RuntimeException("Failed to upload image to Cloudinary. Response: " + data);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file bytes or upload to Cloudinary", e);
    }
  }

  @Override
  public Map uploadPictureByFile(MultipartFile file, Long size) {
    if (!imageHandlerPortInput.isImage(file)) {
      throw new CustomException("Input not image", HttpStatus.BAD_REQUEST);
    }

    if (!imageHandlerPortInput.checkSizeValid(file, ImageHandlerPortInput.MAX_SIZE_NOT_VALID)) {
      throw new CustomException("Size not valid", HttpStatus.BAD_REQUEST);
    }

//    MultipartFile multipartFile = imageHandlerPortInput.compressImage(file, size);
    return uploadPictureByFile(file);
  }

  @Override
  public String extractUrl(Map data) {
    return this.extractByKey(data, "url");
  }

  @Override
  public String extractPublicId(Map data) {
    return this.extractByKey(data, "public_id");
  }

  @Override
  public String extractByKey(Map data, String key) {
    if (data.get(key) == null)
      throw new RuntimeException("Failed to extract " + key + " from Cloudinary");
    return (String) data.get(key);
  }

  private String extractPublicIdFromUrl(String imageUrl) {
    String withoutExtension = imageUrl.substring(0, imageUrl.lastIndexOf('.'));

    return withoutExtension.substring(withoutExtension.lastIndexOf('/') + 1);
  }

  @Override
  public boolean deletePictureByUrl(String url) {
    if (url == null)
      return true;
    try {
      String publicId = extractPublicIdFromUrl(url);

      Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

      return "ok".equals(result.get("result"));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
