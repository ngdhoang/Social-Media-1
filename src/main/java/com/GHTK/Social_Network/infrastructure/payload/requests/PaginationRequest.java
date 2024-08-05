package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.Enum.EOrderBy;
import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidOrderBy;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidSortBy;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaginationRequest {
  @Min(value = 0, message = "Page must be greater than or equal to 0")
  @Max(value = 100, message = "Page must be less than or equal to 1000")
  private int page = 0;

  @Min(value = 1, message = "size must be greater than or equal to 1")
  private int size = 10;

  @Enumerated(EnumType.STRING)
  @ValidSortBy
  private String sortBy = ESortBy.CREATED_AT.name();

  @Enumerated(EnumType.STRING)
  @ValidOrderBy
  private String orderBy = EOrderBy.DESC.name();
}
