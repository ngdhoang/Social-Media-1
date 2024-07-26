package com.GHTK.Social_Network.infrastructure.payload.responses;

//{
//        "roleId":
//        "role": "comment|post"
//        "owner": {
//        "userId":
//        "firstName":
//        "lastName":
//        "avatar":
//        }
//        "reactionType: ""
//        "content": "hello world",
//        "createUp": "2024-07-24T08:06:53.298+00:00",
//        "updateUp": "2024-07-24T08:06:53.298+00:00",
//        "imageUrl": "http://image"
//}

import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteractionResponse {
  private Long roleId;

  private String role;

  private UserBasicDto owner;

  private EReactionType reactionType;

  private String content;

  private String image;

  private LocalDate createdAt;

  private LocalDate updateAt;
}
