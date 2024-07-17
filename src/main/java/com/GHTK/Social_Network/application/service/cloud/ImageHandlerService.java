package com.GHTK.Social_Network.application.service.cloud;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageHandlerService implements ImageHandlerPortInput {
  @Override
  public byte[] base64ToByte(String base64) {
    String base64String = base64.split(",")[1].trim();
    return Base64.getDecoder().decode(base64String);
  }

  @Override
  public boolean isBase64(String input) {
    if (input == null || input.isEmpty()) {
      return false;
    }

    String base64Data = input;
    if (input.contains(",")) {
      String[] parts = input.split(",", 2);
      if (parts.length > 1) {
        base64Data = parts[1];
      }
    }

    base64Data = base64Data.replaceAll("\\s", "");

    if (base64Data.length() % 4 != 0) {
      return false;
    }

    return base64Data.matches("^[A-Za-z0-9+/]*={0,2}$");
  }

  @Override
  public long base64ImageSizeCalculator(String base64) {
    byte[] bytes = base64ToByte(base64);
    return bytes.length;
  }

  @Override
  public boolean checkSizeValid(String base64, long maxSize) {
    return base64ImageSizeCalculator(base64) < maxSize;
  }

  @Override
  public boolean isImage(String base64) {
    if (!isBase64(base64)) {
      return false;
    }

    byte[] decodedBytes = base64ToByte(base64);
    String mimeType = getMimeTypeFromBytes(decodedBytes);

    Set<String> validImageMimeTypes = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    ));

    return validImageMimeTypes.contains(mimeType);
  }

  @Override
  public MultipartFile compressImage(String base64, long maxSize) {
    try {
      byte[] imageBytes = base64ToByte(base64);
      ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
      BufferedImage originalImage = ImageIO.read(bais);

      float quality = 1.0f;
      ByteArrayOutputStream baos;

      do {
        baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        writer.write(null, new IIOImage(originalImage, null, null), param);
        writer.dispose();
        ios.close();

        quality -= 0.1f;
      } while (baos.size() > maxSize && quality > 0.1f);

      byte[] compressedBytes = baos.toByteArray();
      String compressedBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(compressedBytes);

      return convertBase64ToMultipartFile(compressedBase64);

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private String getMimeTypeFromBytes(byte[] bytes) {
    if (bytes.length < 8) {
      return null;
    }

    if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
      return "image/jpeg";
    } else if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47) {
      return "image/png";
    } else if (bytes[0] == (byte) 0x47 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46) {
      return "image/gif";
    } else if (bytes[0] == (byte) 0x42 && bytes[1] == (byte) 0x4D) {
      return "image/bmp";
    } else if (bytes[0] == (byte) 0x52 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46 && bytes[3] == (byte) 0x46 &&
            bytes[8] == (byte) 0x57 && bytes[9] == (byte) 0x45 && bytes[10] == (byte) 0x42 && bytes[11] == (byte) 0x50) {
      return "image/webp";
    }

    return null;
  }

  @Override
  public MultipartFile convertBase64ToMultipartFile(String base64String) {
    byte[] bytes = base64ToByte(base64String);
    return new BASE64DecodedMultipartFile(bytes);
  }
}