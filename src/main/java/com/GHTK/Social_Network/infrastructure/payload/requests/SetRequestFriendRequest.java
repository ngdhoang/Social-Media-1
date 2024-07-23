package com.GHTK.Social_Network.infrastructure.payload.requests;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Data
public class SetRequestFriendRequest {
  private Long userReceiveId;

  @NotNull(message = "status cannot blank")
  @Min(value = 1, message = "status must be greater than or equal to 1")
  @Max(value = 5, message = "status must be less than or equal to 5")
  private Integer status;
}