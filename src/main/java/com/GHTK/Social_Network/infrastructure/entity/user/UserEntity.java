package com.GHTK.Social_Network.infrastructure.entity.user;

import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.entity.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.comment.ImageCommentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

  private LocalDate dob;

  private String phoneNumber;

  private String homeTown;

  private String schoolName;

  private String workPlace;

  private Boolean isProfilePublic = true;

  @Enumerated(EnumType.STRING)
  private EStatusUser statusUser;

  @Enumerated(EnumType.STRING)
  private ERole role;

//  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
//          cascade = CascadeType.ALL)
//  private List<Devices> devicesList;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TokenEntity> tokenEntities;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<PostEntity> postEntities;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUserEntity> tagUserEntities;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<FriendShipEntity> friendShips;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPost> reactionPosts;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<CommentEntity> comments;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImageCommentEntity> imageComments;

  public UserEntity(String firstName, String lastName, String userEmail, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userEmail = userEmail;
    this.password = password;
  }
}
