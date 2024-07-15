package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.SearchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SearchMapper {
  SearchMapper INSTANCE = Mappers.getMapper(SearchMapper.class);

  @Mapping(source = "userId", target = "userId")
  SearchDto UserToSearchDto(User user);
}
