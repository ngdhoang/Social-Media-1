package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "user")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String firstName;

  private String lastName;

  private String userEmail;

  private String password;

  private String oldPassword;

  private String avatar;

  private String background;

  private Boolean isProfilePublic = true;

  @Enumerated(EnumType.STRING)
  private EStatusUser statusUser;

  @Enumerated(EnumType.STRING)
  private ERoleEntity role;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TokenEntity> tokenEntities;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<PostEntity> postEntities;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUserEntity> tagUserEntities;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<FriendShipEntity> friendShipEntities;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPostEntity> reactionPostEntities;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionCommentEntity> reactionCommentEntities;


  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<CommentEntity> commentEntities;

  @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
  private ProfileEntity profile;

  public UserEntity(String firstName, String lastName, String userEmail, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userEmail = userEmail;
    this.password = password;
  }

  @PrePersist
  public void prePersist() {
    if (this.profile == null) {
      this.profile = new ProfileEntity();
      this.profile.setUserEntity(this);
    }
  }
}
