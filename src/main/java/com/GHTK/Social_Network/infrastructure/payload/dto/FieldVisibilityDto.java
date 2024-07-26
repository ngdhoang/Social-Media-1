package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldVisibilityDto<T> {
  private T value;
  private boolean visibility;
}
