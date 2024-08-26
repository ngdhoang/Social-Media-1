package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

@Node("Post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostNode {
    @Id
    private Long id;

    private Long postId;

    private LocalDateTime createAt;

    private EPostStatusEntity postStatus;

    private LocalDateTime lastUpdateAt;

    private Long ownerId;

    public PostNode(Long postId, EPostStatusEntity postStatus, LocalDateTime createAt) {
        this.postId = postId;
        this.postStatus = postStatus;
        this.createAt = createAt;
        this.lastUpdateAt = Instant.now().atZone(ZonedDateTime.now().getZone()).toLocalDateTime();
    }
}

