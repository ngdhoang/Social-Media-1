package com.GHTK.Social_Network.infrastructure.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneMultipartFileDto {
//  @NotNull(message = "File cannot null")
//  @NotBlank(message = "File cannot blank")
  private MultipartFile file;
}
