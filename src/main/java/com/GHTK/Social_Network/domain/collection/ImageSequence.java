package com.GHTK.Social_Network.domain.collection;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageSequence {
  @Id
  private Long postId;

  private List<Long> listImageSort;
}
