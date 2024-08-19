package com.GHTK.Social_Network.infrastructure.payload.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldVisibilityDto<T> {
  private T value;
  private boolean visibility;
}
