package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.SearchUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAdapter implements SearchPort {
    private final UserRepository userRepository;

    private final UserNodeRepository userNodeRepository;

    private final UserMapperETD userMapperETD;

    @Override
    public List<User> searchUserInPage(String keyword) {
        return userRepository.searchUsersByNameOrEmail(keyword).stream().map(
                userMapperETD::toDomain
        ).toList();
    }

    @Override
    public List<Long> searchUser(SearchUserRequest searchUserRequest, Long userId) {
        Pageable pageable = searchUserRequest.toPageableNotSort();
        List<Long> users = userNodeRepository.searchUserFullTextSearch(searchUserRequest.getKeyword(), userId, pageable);
        System.out.println(users);
        return users;
    }
}
