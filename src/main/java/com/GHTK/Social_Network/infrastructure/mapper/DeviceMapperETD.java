package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.user.Device;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceMapperETD {
  @Mapping(source = "deviceId", target = "deviceId")
  @Mapping(source = "userEntity.userId", target = "userId")
  Device toDomain(DeviceEntity entity);

  @Mapping(target = "userEntity", ignore = true)
  DeviceEntity toEntity(Device dto);
}
