package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Table(name = "friend_ship")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendShipId;

    @Enumerated(EnumType.STRING)
    private EFriendshipStatusEntity friendshipStatus;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createAt;

    @Column(nullable = false)
    private Long userReceiveId;

    @Column(nullable = false)
    private Long userInitiatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userInitiatorId", referencedColumnName = "UserId", insertable = false, updatable = false)
    private UserEntity userEntity;

    @PrePersist
    public void prePersist() {
        createAt = Instant.now();
    }

    public FriendShipEntity(Long userReceiveId, Long userInitiatorId, EFriendshipStatusEntity friendshipStatus) {
        this.userReceiveId = userReceiveId;
        this.userInitiatorId = userInitiatorId;
        this.friendshipStatus = friendshipStatus;
    }
}
