package com.GHTK.Social_Network.infrastructure.payload.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchUserRequest extends PaginationRequest {
    @NotBlank(message = "keyword cannot blank")
    private String keyword;
}