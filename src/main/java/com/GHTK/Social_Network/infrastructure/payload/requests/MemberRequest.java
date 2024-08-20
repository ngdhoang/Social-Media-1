package com.GHTK.Social_Network.infrastructure.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {
  private String groupId;

  private List<Long> memberId;
}
