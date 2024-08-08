package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.domain.UserWsDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionWsDto {
  private List<UserWsDetails> sessions;
}
