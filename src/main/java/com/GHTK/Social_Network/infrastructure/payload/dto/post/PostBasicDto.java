package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostBasicDto {
    private Long postId;

    private String postStatus;

    private List<ImageDto> imagePosts;
}
