package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import com.GHTK.Social_Network.domain.entity.EPostStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PostCreateRequest {


    private String content;

    private LocalDate createdAt;

    private EPostStatus postStatus;
}
