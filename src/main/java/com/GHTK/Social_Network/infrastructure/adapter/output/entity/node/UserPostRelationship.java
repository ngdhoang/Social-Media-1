package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostRelationship {
    @RelationshipId
    private Long id;

    @TargetNode
    private PostNode post;

    private Integer score;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now(ZoneId.systemDefault());
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.systemDefault());
    }
}
