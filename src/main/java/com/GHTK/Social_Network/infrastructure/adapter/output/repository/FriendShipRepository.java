package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.domain.entity.FriendShip;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

//    @Query(value = """
//            select f from FriendShip f
//            where (f.userReceiveId = :userId or f.userInitiatorId = :userId)
//            and f.friendshipStatus = :status
//            order by :orderBy :sortBy
//            limit :limit offset :offset
//            """)
//    FriendShip[] getFriendShip(@Param("userId") Long userId, @Param("status") String status, @Param("limit") Integer limit, @Param("offset") Integer offset, @Param("orderBy") String orderBy, @Param("sortBy") String sortBy);

    @Query(value = """
            select f from FriendShip f
            where f.userReceiveId = :userReceiveId 
            and f.userInitiatorId = :userInitiateId
            """)
    FriendShip findFriendShip(@Param("userInitiateId") Long userInitiateId, @Param("userReceiveId") Long userReceiveId);

    @Modifying
    @Transactional
    @Query(value = """
            update FriendShip f
            set f.friendshipStatus = :status
            where f.userReceiveId = :userReceiveId
            and f.userInitiatorId = :userInitiateId
            """)
    void setRequestFriendShip(@Param("userReceiveId") Long userReceiveId, @Param("userInitiateId") Long userInitiateId, @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = """
            update FriendShip f
            set f.friendshipStatus = :status
            where f.friendShipId = :friendShipId
            """)
    void setRequestFriendShip(@Param("friendShipId") Long friendShipId, @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = """
            insert into FriendShip (user_receive_id, user_initiator_id, friendship_status)
            values (:userReceiveId, :userInitiateId, :status)
            """, nativeQuery = true)
    void addFriendShip(@Param("userReceiveId") Long userReceiveId, @Param("userInitiateId") Long userInitiateId, @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = """
            delete from FriendShip f
            where f.friendShipId = :friendShipId
            """)
    void delete(@Param("friendShipId") Long friendShipId);

}
