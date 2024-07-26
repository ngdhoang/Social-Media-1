package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionPostRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionPostMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionPostTypeMapperETD;
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
public class ReactionPostAdapter implements ReactionPostPort {
    private final ReactionPostRepository reactionPostRepository;
    private final ReactionPostMapperETD reactionPostMapperETD;
    private final ReactionPostTypeMapperETD reactionPostTypeMapperETD;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ReactionPost findByPostIdAndUserID(Long postId, Long userId) {
        return reactionPostMapperETD.toDomain(reactionPostRepository.findByPostIdAndUserID(postId, userId));
    }

    @Override
    public ReactionPost saveReaction(ReactionPost reactionPost) {
        if (reactionPost.getReactionType() == null) {
            reactionPost.setReactionType(EReactionType.LIKE);
        }
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
    public List<Map<EReactionType, Set<ReactionPost>>> getReactionGroupByPostId(Long postId) {

        List<Map<String, Object>> list = reactionPostRepository.getReactionGroupByPostId(postId);
        // [
        //  {reactionType : LIKE, reaction_posts : [{"post_id": 1, "user_id": 1, "create_at": "2024-07-24", "reaction_post_id": 2}, {"post_id": 1, "user_id": 2, "create_at": "2024-07-24", "reaction_post_id": 3}]},
        //  {reactionType : LOVE, reaction_posts : [{"post_id": 1, "user_id": 3, "create_at": "2024-07-24", "reaction_post_id": 4}]}
        // ]

        List<Map<EReactionType, Set<ReactionPost>>> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            EReactionType reactionType = EReactionType.valueOf((String) map.get("reactionType"));

            String reactionPostsJson = (String) map.get("reaction_posts");
            //[{"post_id": 1, "user_id": 1, "create_at": "2024-07-24", "reaction_post_id": 2}, {"post_id": 1, "user_id": 2, "create_at": "2024-07-24", "reaction_post_id": 3}]
            List<Map<String, Object>> reactionPosts = handleJsonString(reactionPostsJson);

            Set<ReactionPost> reactionPostSet = reactionPosts.stream().map(
                    reactionPost -> {
                        ReactionPost reactionPost1 = new ReactionPost();
                        reactionPost1.setPostId(convertToLong(reactionPost.get("post_id")));
                        reactionPost1.setUserId(convertToLong(reactionPost.get("user_id")));
                        String createdAtStr = (String) reactionPost.get("create_at");
                        LocalDate createdAt = LocalDate.parse(createdAtStr, DATE_FORMATTER);
                        reactionPost1.setCreatedAt(createdAt);
                        reactionPost1.setReactionPostId(convertToLong(reactionPost.get("reaction_post_id")));
                        reactionPost1.setReactionType(reactionType);
                        return reactionPost1;
                    }
            ).collect(Collectors.toSet());
            result.add(Map.of(reactionType, reactionPostSet));

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


    @Override
    public ReactionPost findReactionCommentByCommentIdAndUserId(Long commentId, Long userId) {
        return reactionPostMapperETD.toDomain(reactionPostRepository.findByCommentIdAndUserId(commentId, userId));
    }

}
