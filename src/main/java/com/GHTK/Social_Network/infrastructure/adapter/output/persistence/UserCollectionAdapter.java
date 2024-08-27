package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.UserCollectionPort;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserCollectionAdapter implements UserCollectionPort {
  private final MongoTemplate mongoTemplate;

  private final UserCollectionMapperETD userCollectionMapperETD;

  @Override
  public UserGroup getUserGroupByUserId(Long userId, String groupId) {
    Criteria criteria = Criteria.where("userId").is(userId)
            .and("userGroupInfoList.groupId").is(groupId);
    Query query = new Query(criteria);

    UserCollection userCollection = mongoTemplate.findOne(query, UserCollection.class);
    if (userCollection == null) {
      return null;
    }

    return userCollectionMapperETD.toDomain(userCollection)
            .getUserGroupInfoList()
            .stream()
            .filter(userGroup -> userGroup.getGroupId().equals(groupId))
            .findFirst()
            .orElse(null);
  }

  @Override
  public void addUserGroup(Long userId, UserGroup newUserGroup) {
    removeUserGroup(userId, newUserGroup.getGroupId());
    UserGroupInfo userGroupInfo = userCollectionMapperETD.userGroupToUserGroupInfo(newUserGroup);

    Query query = new Query(Criteria.where("userId").is(userId));
    Update update = new Update().addToSet("userGroupInfoList", userGroupInfo);

    UpdateResult result = mongoTemplate.upsert(query, update, UserCollection.class);

    if (result.getMatchedCount() == 0) {
      UserCollection userCollection = new UserCollection();
      userCollection.setUserId(userId);
      userCollection.setUserGroupInfoList(Collections.singletonList(userGroupInfo));
      mongoTemplate.insert(userCollection);
    }
  }

  @Override
  public void removeUserGroup(Long userId, String groupId) {
    Query query = new Query(Criteria.where("userId").is(userId));
    Update update = new Update().pull("userGroupInfoList", Query.query(Criteria.where("groupId").is(groupId)));
    mongoTemplate.updateFirst(query, update, UserCollection.class);
  }
}
