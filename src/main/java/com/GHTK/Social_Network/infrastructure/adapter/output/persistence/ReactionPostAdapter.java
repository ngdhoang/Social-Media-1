package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionPostRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionPostMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionPostTypeMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionPostAdapter implements ReactionPostPort {
  private final ReactionPostRepository reactionPostRepository;
  private final ReactionPostMapperETD reactionPostMapperETD;
  private final ReactionPostTypeMapperETD reactionPostTypeMapperETD;

  @Override
  public ReactionPost findByPostIdAndUserID(Long postId, Long userId) {
    return reactionPostMapperETD.toDomain(reactionPostRepository.findByPostIdAndUserID(postId, userId));
  }

  @Override
  public ReactionPost saveReaction(ReactionPost reactionPost) {
    if (reactionPost.getReactionType() == null) {
      reactionPost.setReactionType(EReactionType.LIKE);
    }
    System.out.println("reactionPostMapperETD.toEntity(reactionPost)");
    System.out.println(reactionPostMapperETD.toEntity(reactionPost));
    return reactionPostMapperETD.toDomain(reactionPostRepository.save(reactionPostMapperETD.toEntity(reactionPost)));
  }

  @Override
  public void deleteReaction(ReactionPost reactionPost) {
    reactionPostRepository.delete(reactionPostMapperETD.toEntity(reactionPost));
  }

  @Override
  public List<ReactionPost> findByPostId(Long postId) {
    return reactionPostRepository.findByPostId(postId).stream().map(reactionPostMapperETD::toDomain).toList();
  }

  @Override
  public int countReactionByPostId(Long postId) {
    return reactionPostRepository.countReactionByPostId(postId);
  }

  @Override
  public int countReactionByPostIdAndType(Long postId, EReactionType reactionType) {
    return reactionPostRepository.countReactionByPostIdAndType(postId, reactionPostTypeMapperETD.toEntity(reactionType));
  }

  @Override
  public Map<EReactionType, Set<ReactionPost>> getReactionGroupByPostId(Long postId){
//    return reactionPostRepository.getReactionGroupByPostId(postId).entrySet().stream()
//            .collect(Collectors.toMap(
//                    entry -> reactionPostTypeMapperETD.toDomain(EReactionTypeEntity.valueOf(entry.getKey())),
//                    entry -> ((List<Map<String, Object>>) entry.getValue()).stream()
//                            .map(map -> ReactionPost.builder()
//                                    .userId((Long) map.get("userId"))
//                                    .postId((Long) map.get("postId"))
//                                    .build())
//                            .collect(Collectors.toSet())
//            ));

    Map<String, Object> map = reactionPostRepository.getReactionGroupByPostId(postId);

    System.out.println("map");
    map.forEach((key, value) -> {
      System.out.println("key: " + key + " value: " + value);
    });

    return map.entrySet().stream()
            .collect(Collectors.toMap(
                    entry -> reactionPostTypeMapperETD.toDomain(EReactionTypeEntity.valueOf(entry.getKey())),
                    entry -> ((List<Map<String, Object>>) entry.getValue()).stream()
                            .map(map1 -> ReactionPost.builder()
                                    .userId((Long) map1.get("userId"))
                                    .postId((Long) map1.get("postId"))
                                    .build())
                            .collect(Collectors.toSet())
            ));

  }

  @Override
  public List<ReactionPost> getByPostIdAndType(Long postId, GetReactionPostRequest getReactionPostRequest) {
    int page = getReactionPostRequest.getPage();
    int size = getReactionPostRequest.getSize();
    String orderBy = getReactionPostRequest.getOrderBy();
    String sortBy = getReactionPostRequest.getSortBy();
    EReactionType reactionType = getReactionPostRequest.getReactionType() == null ? null : EReactionType.valueOf(getReactionPostRequest.getReactionType());
    sortBy = sortBy.equals(ESortBy.CREATED_AT.toString()) ? "createAt" : "reactionPostId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    return reactionPostRepository.getByPostIdAndType(postId, reactionPostTypeMapperETD.toEntity(reactionType), pageable).stream().map(reactionPostMapperETD::toDomain).toList();
  }


  @Override
  public List<ReactionPost> getListReactionByPostId(Long postId, GetReactionPostRequest getReactionPostRequest) {
    int page = getReactionPostRequest.getPage();
    int size = getReactionPostRequest.getSize();
    String orderBy = getReactionPostRequest.getOrderBy();
    String sortBy = getReactionPostRequest.getSortBy();
    EReactionType reactionType = getReactionPostRequest.getReactionType() == null ? null : EReactionType.valueOf(getReactionPostRequest.getReactionType());
    sortBy = sortBy.equals(ESortBy.CREATED_AT.toString()) ? "createAt" : "reactionPostId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    if (reactionType == null) {
      return reactionPostRepository.getByPostId(postId, pageable).stream().map(reactionPostMapperETD::toDomain).toList();
    }
    return reactionPostRepository.getByPostIdAndType(postId, reactionPostTypeMapperETD.toEntity(reactionType), pageable).stream().map(reactionPostMapperETD::toDomain).toList();
  }

}
