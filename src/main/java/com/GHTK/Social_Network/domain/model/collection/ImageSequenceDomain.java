package com.GHTK.Social_Network.domain.model.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageSequenceDomain {
  private Long postId;

  private List<Long> listImageSort;
}
