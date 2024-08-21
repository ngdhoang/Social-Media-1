package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<GroupCollection, String> {
  @Query("{ 'groupName': ?0 }")
  Optional<GroupCollection> findByGroupName(String name);

  @Query("{'userId': ?0}")
  List<GroupCollection> findAllByUserId (Long userID, Pageable pageable);
}

