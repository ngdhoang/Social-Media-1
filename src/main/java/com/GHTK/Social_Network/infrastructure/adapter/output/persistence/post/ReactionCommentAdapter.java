package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.ReactionCommentPort;
import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.ReactionComment;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionCommentRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionCommentMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionTypeMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionCommentAdapter implements ReactionCommentPort {
  private final ReactionCommentRepository reactionCommentRepository;
  private final ReactionCommentMapperETD reactionCommentMapperETD;
  private final ReactionTypeMapperETD reactionTypeMapperETD;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public ReactionComment findByCommentIdAndUserID(Long commentId, Long userId) {
    return reactionCommentMapperETD.toDomain(reactionCommentRepository.findByCommentIdAndUserID(commentId, userId));
  }

  @Override
  public ReactionComment saveReaction(ReactionComment reactionComment) {
    if (reactionComment.getReactionType() == null) {
      reactionComment.setReactionType(EReactionType.LIKE);
    }
    return reactionCommentMapperETD.toDomain(reactionCommentRepository.save(reactionCommentMapperETD.toEntity(reactionComment)));
  }

  @Override
  public void deleteReaction(ReactionComment reactionComment) {
    reactionCommentRepository.delete(reactionCommentMapperETD.toEntity(reactionComment));
  }

  @Override
  public List<ReactionComment> findByCommentId(Long commentId) {
    return reactionCommentRepository.findByCommentId(commentId).stream().map(reactionCommentMapperETD::toDomain).toList();
  }

  @Override
  public int countReactionByCommentId(Long commentId) {
    return reactionCommentRepository.countReactionByCommentId(commentId);
  }

  @Override
  public int countReactionByCommentIdAndType(Long commentId, EReactionType reactionType) {
    return reactionCommentRepository.countReactionByCommentIdAndType(commentId, reactionTypeMapperETD.toEntity(reactionType));
  }

  @Override
  public List<Map<EReactionType, Set<ReactionComment>>> getReactionGroupByCommentId(Long commentId) {

    List<Map<String, Object>> list = reactionCommentRepository.getReactionGroupByCommentId(commentId);

    List<Map<EReactionType, Set<ReactionComment>>> result = new ArrayList<>();
    for (Map<String, Object> map : list) {
      EReactionType reactionType = EReactionType.valueOf((String) map.get("reactionType"));

      String reactionCommentsJson = (String) map.get("reaction_comments");
      //[{"comment_id": 1, "user_id": 1, "create_at": "2024-07-24", "reaction_comment_id": 2}, {"comment_id": 1, "user_id": 2, "create_at": "2024-07-24", "reaction_comment_id": 3}]
      List<Map<String, Object>> reactionComments = handleJsonString(reactionCommentsJson);

      Set<ReactionComment> reactionCommentSet = reactionComments.stream().map(
              reactionComment -> {
                ReactionComment reactionComment1 = new ReactionComment();
                reactionComment1.setCommentId(convertToLong(reactionComment.get("comment_id")));
                reactionComment1.setUserId(convertToLong(reactionComment.get("user_id")));
                String createdAtStr = (String) reactionComment.get("create_at");
                LocalDate createdAt = LocalDate.parse(createdAtStr, DATE_FORMATTER);
                reactionComment1.setCreatedAt(createdAt);
                reactionComment1.setReactionCommentId(convertToLong(reactionComment.get("reaction_comment_id")));
                reactionComment1.setReactionType(reactionType);
                return reactionComment1;
              }
      ).collect(Collectors.toSet());
      result.add(Map.of(reactionType, reactionCommentSet));

    }
    return result;
  }

  public List<Map<String, Object>> handleJsonString(String jsonString) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private Long convertToLong(Object value) {
    if (value instanceof Integer) {
      return ((Integer) value).longValue();
    } else if (value instanceof Long) {
      return (Long) value;
    } else {
      throw new IllegalArgumentException("Unsupported type for conversion: " + value.getClass());
    }
  }

  @Override
  public List<ReactionComment> getByCommentIdAndType(Long commentId, GetReactionCommentRequest getReactionCommentRequest) {
    int page = getReactionCommentRequest.getPage();
    int size = getReactionCommentRequest.getSize();
    String orderBy = getReactionCommentRequest.getOrderBy();
    String sortBy = getReactionCommentRequest.getSortBy();
    EReactionType reactionType = getReactionCommentRequest.getReactionType() == null ? null : EReactionType.valueOf(getReactionCommentRequest.getReactionType());
    sortBy = sortBy.equals(ESortBy.CREATED_AT.toString()) ? "createAt" : "reactionCommentId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    return reactionCommentRepository.getByCommentIdAndType(commentId, reactionTypeMapperETD.toEntity(reactionType), pageable).stream().map(reactionCommentMapperETD::toDomain).toList();
  }


  @Override
  public List<ReactionComment> getListReactionByCommentId(Long commentId, GetReactionCommentRequest getReactionCommentRequest) {
    int page = getReactionCommentRequest.getPage();
    int size = getReactionCommentRequest.getSize();
    String orderBy = getReactionCommentRequest.getOrderBy();
    String sortBy = getReactionCommentRequest.getSortBy();
    EReactionType reactionType = getReactionCommentRequest.getReactionType() == null ? null : EReactionType.valueOf(getReactionCommentRequest.getReactionType());
    sortBy = sortBy.equals(ESortBy.CREATED_AT.toString()) ? "createAt" : "reactionCommentId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    if (reactionType == null) {
      return reactionCommentRepository.getByCommentId(commentId, pageable).stream().map(reactionCommentMapperETD::toDomain).toList();
    }
    return reactionCommentRepository.getByCommentIdAndType(commentId, reactionTypeMapperETD.toEntity(reactionType), pageable).stream().map(reactionCommentMapperETD::toDomain).toList();
  }

}