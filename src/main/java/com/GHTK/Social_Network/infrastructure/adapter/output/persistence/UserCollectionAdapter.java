package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.UserCollectionPort;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCollectionAdapter implements UserCollectionPort {
  private final MongoTemplate mongoTemplate;

  private final UserCollectionMapperETD userCollectionMapperETD;

  @Override
  public UserGroup getUserGroupByUserId(Long userId, String groupId) {
    Query query = new Query(Criteria.where("userId").is(userId));
    Query.query(Criteria.where("groupId").is(groupId));

    return userCollectionMapperETD.toDomain(mongoTemplate.findOne(query, UserCollection.class)).getUserGroupInfoList().get(0);
  }

  @Override
  public void addUserGroup(Long userId, UserGroup newUserGroup) {
    UserGroupInfo userGroupInfo = userCollectionMapperETD.userGroupToUserGroupInfo(newUserGroup);
    Query query = new Query(Criteria.where("userId").is(userId));
    Update update = new Update().push("userGroupInfoList", userGroupInfo);
    mongoTemplate.updateFirst(query, update, UserCollection.class);
  }

  @Override
  public void removeUserGroup(Long userId, String groupId) {
    Query query = new Query(Criteria.where("userId").is(userId));
    Update update = new Update().pull("userGroupInfoList", Query.query(Criteria.where("groupId").is(groupId)));
    mongoTemplate.updateFirst(query, update, UserCollection.class);
  }
}
