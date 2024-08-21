package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MemberCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.GroupRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.mapper.GroupMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.MemberMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupAdapter implements GroupPort {
    private final MongoTemplate mongoTemplate;

    private final UserCollectionRepository userCollectionRepository;
    private final GroupRepository groupRepository;
    private final UserCollectionRepository userRepository;

    private final GroupMapperETD groupMapperETD;
    private final MemberMapperETD memberMapperETD;
    private final UserCollectionMapperETD userCollectionMapperETD;

    @Override
    public UserCollectionDomain saveUser(UserCollectionDomain user) {
        UserCollection u = userRepository.findByUserId(user.getUserId());
        user.setId(u.getId());
        return userCollectionMapperETD.toDomain(
                userRepository.save(userCollectionMapperETD.toEntity(user))
        );

    }

    @Override
    public Group saveGroup(Group newGroup) {
        GroupCollection newGroupCollection = groupMapperETD.toEntity(newGroup);
        GroupCollection savedGroupCollection = groupRepository.save(newGroupCollection);
        return groupMapperETD.toDomain(savedGroupCollection);
    }

    @Override
    public Group createGroupPersonal(Long userSendId, Long userReceiveId) {
        String groupName = userSendId < userReceiveId
                ? String.format("%d_%d", userSendId, userReceiveId)
                : String.format("%d_%d", userReceiveId, userSendId);

        List<Member> members = Arrays.asList(
                Member.builder().userId(userSendId).build(),
                Member.builder().userId(userReceiveId).build()
        );

        setMemberInUser(userSendId, groupName);
        setMemberInUser(userReceiveId, groupName);

        Group newGroup = Group.builder()
                .groupName(groupName)
                .groupType(EGroupType.PERSONAL)
                .members(members)
                .build();

        return saveGroup(newGroup);
    }

    @Override
    public Group getGroupForPersonal(String groupName) {
        Optional<GroupCollection> optionalGroup = groupRepository.findByGroupName(groupName);
        return optionalGroup.map(groupMapperETD::toDomain).orElse(null);
    }

    @Override
    public Group getGroupForGroup(String groupId) {
        Optional<GroupCollection> optionalGroup = groupRepository.findById(groupId);
        return optionalGroup.map(groupMapperETD::toDomain).orElse(null);
    }

    @Override
    public boolean isUserInGroup(Long userId, String groupId) {
        Group group = getGroupForPersonal(groupId);
        if (group == null) {
            group = getGroupForGroup(groupId);
        }
        return group.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));

    }

    @Override
    public List<Group> getMyGroups(Long userId, PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable();
        return groupRepository.findAllByUserId(userId, pageable).stream().map(
                        g -> {

                            // Nếu không phải nhóm cá nhân, trả về nhóm bình thường
                            return groupMapperETD.toDomain(g);
                        }
                )
                .toList();
    }

    @Override
    public UserCollectionDomain getUserGroups(Long userId, int skip, int limit) {
        Query query = new Query(Criteria.where("userId").is(userId));
        query.fields().include("userGroupInfoList").slice("userGroupInfoList", skip, limit);
        UserCollection result = mongoTemplate.findOne(query, UserCollection.class);
        return userCollectionMapperETD.toDomain(result);
    }

    private void setMemberInUser(Long userReceiveId, String groupName) {
        UserCollection userReceive = userCollectionRepository.findByUserId(userReceiveId);
        if (userReceive.getUserGroupInfoList() == null) {
            userReceive.setUserGroupInfoList(new ArrayList<>());
        }
        userReceive.getUserGroupInfoList().add(
                new UserGroupInfo(groupName, null, null)
        );
        userCollectionRepository.save(userReceive);
    }

    @Override
    public Group createGroupGroup(Long userSendId, List<Long> userReceiveIds) {
        String groupName = String.format("%d_%s", userSendId, userReceiveIds.toString());

        List<Member> members = new ArrayList<>();
        // Add the sender to the group
        members.add(Member.builder().userId(userSendId).build());

        // Add all the recipients to the group
        for (Long userId : userReceiveIds) {
            members.add(Member.builder().userId(userId).build());
        }

        Group newGroup = Group.builder()
                .groupName(groupName)
                .members(members)
                .build();

        return saveGroup(newGroup);
    }

    @Override
    public Group setMemberRole(Long userId, String groupId, EStateUserGroup newRole) {
        Group group = getGroupForGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }
        // Find the member with the given userId
        Optional<Member> optionalMember = group.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst();

        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("User is not a member of the group");
        }
        // Update the member's role
        Member member = optionalMember.get();
        member.setRole(EStateUserGroup.valueOf(newRole.toString()));

        // Save the updated group back to the repository
        return saveGroup(group);
    }

    @Override
    public Set<Group> getGroupsByUserId(Long userId) {
        UserCollection userCollection = userCollectionRepository.findByUserId(userId);
        return userCollection.getUserGroupInfoList().stream().map(
                userGroupInfo -> {
                    if (userGroupInfo.getGroupId().contains("_"))
                        return getGroupForPersonal(userGroupInfo.getGroupId());
                    else
                        return getGroupForGroup(userGroupInfo.getGroupId());
                }
        ).collect(Collectors.toSet());
    }

    @Override
    public void removeMemberByUserId(String groupId, Long userId) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(groupId)));
        Update update = new Update().pull("members", new BasicDBObject("userId", userId));
        mongoTemplate.updateFirst(query, update, GroupCollection.class);
    }

    @Override
    public Member getMemberByUserId(String groupId, Long userId) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(groupId))
                .and("members.userId").is(userId));
        query.fields().include("members.$");
        GroupCollection group = mongoTemplate.findOne(query, GroupCollection.class);
        MemberCollection memberCollection = group != null && !group.getMembers().isEmpty() ? group.getMembers().get(0) : null;
        return memberMapperETD.toDomain(Objects.requireNonNull(memberCollection));
    }

    @Override
    public void addMember(String groupId, Member newMember) {
        MemberCollection memberCollection = memberMapperETD.toEntity(newMember);

        // Tạo query để tìm nhóm và kiểm tra thành viên đã tồn tại
        Query query = new Query(
                Criteria.where("_id").is(new ObjectId(groupId))
                        .and("members.memberId").is(memberCollection.getUserId())
        );

        // Tìm kiếm nhóm với thành viên có sẵn
        GroupCollection existingGroup = mongoTemplate.findOne(query, GroupCollection.class);

        if (existingGroup != null) {
            // Thành viên đã tồn tại, thực hiện cập nhật thông tin thành viên
            Update update = new Update()
                    .set("members.$.name", memberCollection.getNickname())
                    .set("members.$.role", memberCollection.getRole())
                    // thêm các trường khác cần update...
                    ;
            mongoTemplate.updateFirst(query, update, GroupCollection.class);
        } else {
            // Thành viên chưa tồn tại, thêm thành viên mới
            query = new Query(Criteria.where("_id").is(new ObjectId(groupId)));
            Update update = new Update().push("members", memberCollection);
            mongoTemplate.updateFirst(query, update, GroupCollection.class);
        }
    }

        @Override
        public Member getLastMember (String groupId){
            // Query to match the group by its ID
            Query query = new Query(Criteria.where("_id").is(new ObjectId(groupId)));

            // Only retrieve the last member in the members list
            query.fields().include("members").slice("members", -1);

            // Execute the query
            GroupCollection group = mongoTemplate.findOne(query, GroupCollection.class);

            // Retrieve the last member from the members list
            MemberCollection memberCollection = group != null && !group.getMembers().isEmpty()
                    ? group.getMembers().get(0)
                    : null;

            // Convert the MemberCollection object to a domain Member object
            return memberMapperETD.toDomain(Objects.requireNonNull(memberCollection));
    }
}
