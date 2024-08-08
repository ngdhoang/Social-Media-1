package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImagePostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.PostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TagUserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ImagePostMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.PostMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.TagUserMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPortAdapter implements PostPort {
  private final FriendShipPort friendShipPort;

  private final PostRepository postRepository;
  private final TagUserRepository tagUserRepository;
  private final ImagePostRepository imagePostRepository;

  private final PostMapperETD postMapperETD;
  private final TagUserMapperETD tagUserMapperETD;
  private final ImagePostMapperETD imagePostMapperETD;

  @Override
  public Post savePost(Post post) {
    PostEntity postEntity = postMapperETD.toEntity(post);
    return postMapperETD.toDomain(postRepository.save(postEntity));
  }

  @Override
  public Post findPostById(Long id) {
    return postMapperETD.toDomain(postRepository.findById(id).orElse(null));
  }


  @Override
  public List<Post> findPostsByUserIdAndFriendStatus(Long userId, TAKE_POST_STATUS status, List<Long> blockIds, GetPostRequest getPostRequest) {
    String statusString = status.toString();
    Pageable pageable = getPostRequest.toPageableNotSort();
    return postRepository.getListByUserIdAndFriendStatus(userId, statusString, pageable).stream().map(
            postMapperETD::toDomain
    ).toList();
  }

  @Override
  public Post findPostByPostId(Long postId) {
    return postMapperETD.toDomain(postRepository.findById(postId).orElse(null));
  }

  @Override
  public Boolean deletePostById(Long id) {
    try {
      postRepository.deleteById(id);
      return true;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  @Override
  public List<Post> getListPostTagMeNotBlockAndPrivate(Long currentUser, List<Long> blockIds, GetPostRequest getPostRequest) {
    Pageable pageable = getPostRequest.toPageableNotSort();
    List<TagUserEntity> tagUserList = tagUserRepository.getListByUserId(currentUser, blockIds, pageable);
    List<PostEntity> postList = new ArrayList<>();
    tagUserList.forEach(tagUser -> {
      PostEntity p = postRepository.findByTagUsers(tagUser);
      if (p.getUserEntity().getIsProfilePublic() && !friendShipPort.isBlock(currentUser, p.getUserEntity().getUserId())) {
        postList.add(p);
      }
    });
    return postList.stream().map(postMapperETD::toDomain).toList();
  }


  @Override
  public List<Post> findPostsTagMe(Long currentUser, List<Long> blockIds, GetPostRequest getPostRequest) {
    Pageable pageable = getPostRequest.toPageableNotSort();
    List<TagUserEntity> tagUserList = tagUserRepository.getListByUserId(currentUser, blockIds, pageable);
    List<PostEntity> postList = new ArrayList<>();
    tagUserList.forEach(tagUser -> {
      PostEntity p = postRepository.findByTagUsers(tagUser);
      postList.add(p);
    });
    return postList.stream().map(postMapperETD::toDomain).toList();
  }

  @Override
  public List<Post> findPostsWithUserInteractions(Long userId) {
    return postRepository.findPostsWithUserInteractions(userId).stream().map(
            postMapperETD::toDomain
    ).toList();
  }

  @Override
  public Post findPostByImagePostId(Long imagePostId) {
    return postMapperETD.toDomain(postRepository.findByImagePostId(imagePostId));
  }

  @Override
  public TagUser saveTagUser(TagUser tagUser) {
    return tagUserMapperETD.ToDomain(
            tagUserRepository.save(tagUserMapperETD.toEntity(tagUser))
    );
  }

  @Override
  public List<TagUser> saveAllTagUser(List<Long> tagUsers, Long postId) {
    return tagUsers.stream().map(
            tagUserId -> {
              TagUser tagUser = new TagUser();
              tagUser.setUserId(tagUserId);
              tagUser.setPostId(postId);
              return tagUser;
            }
    ).map(
            tagUser -> tagUserMapperETD.ToDomain(
                    tagUserRepository.save(tagUserMapperETD.toEntity(tagUser))
            )
    ).toList();
  }

  @Override
  public List<TagUser> saveAllTagUser(List<TagUser> tagUser) {
    return tagUser.stream().map(this::saveTagUser).toList();
  }

  @Override
  public void deleteAllTagUser(List<TagUser> tagUsers) {
    tagUsers.forEach(
            tagUser -> tagUserRepository.delete(tagUserMapperETD.toEntity(tagUser))
    );
  }

  @Override
  public List<ImagePost> getListImageByPostId(Long postId) {
    return imagePostRepository.findAllByPostId(postId).stream().map(
            imagePostMapperETD::toDomain
    ).toList();
  }

  public List<TagUser> getListTagUserByPostId(Long postId, List<Long> blockIds) {
    return tagUserRepository.getListByPostId(postId, blockIds).stream().map(
            tagUserMapperETD::ToDomain
    ).toList();
  }

  @Override
  public List<Long> getListTagUserIdByPostId(Long postId, List<Long> blockIds) {
    return tagUserRepository.getListUserIdByPostId(postId, blockIds);
  }


  @Override
  public void decrementReactionQuantity(Long postId) {
    postRepository.decreaseReactionsQuantity(postId);
  }

  @Override
  public void incrementReactionQuantity(Long postId) {
    postRepository.increaseReactionsQuantity(postId);
  }

  @Override
  public void decrementCommentQuantity(Long postId, Long numberOfComments) {
    postRepository.decreaseCommentQuantity(postId, numberOfComments);
  }

  @Override
  public void incrementCommentQuantity(Long postId) {
    postRepository.increaseCommentQuantity(postId);
  }
}
