package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "friend_ship")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendShipId;

    @Enumerated(EnumType.STRING)
    private EFriendshipStatus friendshipStatus;

    @Column(columnDefinition = "DATE")
    private LocalDate createAt;

    @Column(nullable = false)
    private Long userReceiveId;

    @Column(nullable = false)
    private Long userInitiatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userInitiatorId", referencedColumnName = "UserId", insertable = false, updatable = false)
    private UserEntity userEntity;

    @PrePersist
    public void prePersist() {
        createAt = LocalDate.now();
    }
}
