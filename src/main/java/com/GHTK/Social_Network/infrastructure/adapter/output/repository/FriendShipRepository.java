package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.FriendShipEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShipEntity, Long> {

  @Query("""
          select f from FriendShipEntity f
          where f.userReceiveId = :userId or f.userInitiatorId = :userId
              and (:status is null and f.friendshipStatus <> 'BLOCK') or f.friendshipStatus = :status
          """)
  List<FriendShipEntity> getListFriend(
          @Param("userId") Long userId,
          @Param("status") EFriendshipStatusEntity status,
          Pageable pageable);


  @Query("""
          select f from FriendShipEntity f
          where f.userInitiatorId = :userId
              and f.friendshipStatus = 'BLOCK'
          """)
  List<FriendShipEntity> getListBlock(
          @Param("userId") Long userId,
          Pageable pageable);

  @Query(value = """
          select f from FriendShipEntity f
          where f.userReceiveId = :userReceiveId 
          and f.userInitiatorId = :userInitiateId
          """)
  FriendShipEntity findFriendShip(@Param("userInitiateId") Long userInitiateId, @Param("userReceiveId") Long userReceiveId);

  @Modifying
  @Transactional
  @Query(value = """
          update FriendShipEntity f
          set f.friendshipStatus = :status
          where f.userReceiveId = :userReceiveId
          and f.userInitiatorId = :userInitiateId
          """)
  void setRequestFriendShip(@Param("userReceiveId") Long userReceiveId, @Param("userInitiateId") Long userInitiateId, @Param("status") String status);

  @Modifying
  @Transactional
  @Query(value = """
          update FriendShipEntity f
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
          delete from FriendShipEntity f
          where f.friendShipId = :friendShipId
          """)
  void delete(@Param("friendShipId") Long friendShipId);

  @Query(value = """
          select case when count(f) > 0 then true else false end
          from FriendShipEntity f
          where 
          (
              (f.userReceiveId = :firstUser and f.userInitiatorId = :secondUser)
              or (f.userReceiveId = :secondUser and f.userInitiatorId = :firstUser)
          )
          and f.friendshipStatus <> 'BLOCK'
          and f.friendshipStatus <> 'PENDING'
        """)
  Boolean isFriend(@Param("firstUser") Long firstUser, @Param("secondUser") Long secondUser);

  @Query(value = """
            select case when count(f) > 0 then true else false end
            from FriendShipEntity f
            where 
            (
                (f.userReceiveId = :firstUser and f.userInitiatorId = :secondUser)
                or (f.userReceiveId = :secondUser and f.userInitiatorId = :firstUser)
            )
            and f.friendshipStatus = 'BLOCK'
            """)
  Boolean isBlock(@Param("firstUser") Long firstUser, @Param("secondUser") Long secondUser);
}