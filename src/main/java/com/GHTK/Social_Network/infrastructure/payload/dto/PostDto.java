package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.domain.entity.EPostStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PostDto {
    private Long postId;

    private String content;

    private LocalDate createdAt;

    private EPostStatus postStatus;
}
