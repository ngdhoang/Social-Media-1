package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.GroupRepository;
import com.GHTK.Social_Network.infrastructure.mapper.GroupMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatAdapter implements ChatPort {
  private final GroupRepository groupRepository;

  private final GroupMapperETD groupMapperETD;

  @Override
  public List<String> getUserIdsFromChannel(String channelId) {
    return new ArrayList<>(1);
  }

  @Override
  public Group createGroup(Group newGroup) {
    GroupCollection newGroupCollection = groupMapperETD.toEntity(newGroup);
    GroupCollection savedGroupCollection = groupRepository.save(newGroupCollection);
    return groupMapperETD.toDomain(savedGroupCollection);
  }

  @Override
  public Group getGroup(String groupId) {
    Optional<GroupCollection> optionalGroup = groupRepository.findById(groupId);
    if (optionalGroup.isPresent()) {
      return groupMapperETD.toDomain(optionalGroup.get());
    } else {
      throw new CustomException("Group don't exist", HttpStatus.NOT_FOUND);
    }
  }
  @Override
  public boolean isUserInGroup(Long userId) {
    return false;
  }

  @Override
  public boolean isExistGroup(String groupId) {
    return groupRepository.existsById(groupId);
  }
}
