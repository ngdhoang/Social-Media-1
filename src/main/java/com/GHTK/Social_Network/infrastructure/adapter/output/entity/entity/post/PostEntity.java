package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post", indexes = {
        @Index(name = "idx_post_user", columnList = "user_id"),
        @Index(name = "idx_post_status", columnList = "postStatus"),
})
public class PostEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private Instant createAt;

  private Instant updateAt;

  private Long reactionsQuantity = 0L;

  private Long commentQuantity = 0L;

  @Enumerated(EnumType.STRING)
  private EPostStatusEntity postStatus;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImagePostEntity> imagePostEntities;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUserEntity> tagUserEntities;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPostEntity> reactionPostEntities;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<CommentEntity> commentEntities;

  @PrePersist
  public void prePersist() {
    this.createAt = Instant.now();
    if (this.reactionsQuantity == null) {
      this.reactionsQuantity = 0L;
    }
    if (this.commentQuantity == null) {
      this.commentQuantity = 0L;
    }
  }

  @PreUpdate
  public void preUpdate() {
    this.updateAt = Instant.now();
  }


}
