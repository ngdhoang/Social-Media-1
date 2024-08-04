package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentBasicDto {
    private Long commentId;

    private String content;

    private LocalDate createAt;

    private String imageUrl;

    private Long parentCommentId;

    private Long postId;
}
