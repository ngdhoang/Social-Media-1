package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.Enum.EOrderBy;
import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidOrderBy;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidSortBy;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PaginationRequest {
  @Min(value = 0, message = "Page must be greater than or equal to 0")
  @Max(value = 100, message = "Page must be less than or equal to 1000")
  private int page = 0;

  @Min(value = 1, message = "size must be greater than or equal to 1")
  private int size = 10;

  @ValidPattern(CustomPatternValidator.SORT_BY)
  private String sortBy = "createAt";

  @ValidPattern(CustomPatternValidator.ORDER_BY)
  private String orderBy = "desc";

  public Pageable toPageable() {
    return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
  }

  public Pageable toPageableNative() {
    if (sortBy.equals("createAt")) {
      sortBy = "create_at";
    }
    if (sortBy.equals("updateAt")) {
      sortBy = "update_at";
    }

    return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
  }

  public Pageable toPageableNotSort() {
    return PageRequest.of(page, size);
  }
}
