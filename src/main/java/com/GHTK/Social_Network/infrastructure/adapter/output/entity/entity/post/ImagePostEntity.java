package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePostEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imagePostId;

  private String imageUrl;

  private Date createAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity postEntity;

  public ImagePostEntity(String imageUrl, Date createAt, PostEntity postEntity) {
    this.imageUrl = imageUrl;
    this.createAt = createAt;
    this.postEntity = postEntity;
  }
}
