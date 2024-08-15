package com.GHTK.Social_Network.domain.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageSequence {
  private String postId;

  private List<Long> listImageSort;
}
