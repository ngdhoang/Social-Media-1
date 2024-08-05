//package com.GHTK.Social_Network.infrastructure.mapper;
//
//import com.GHTK.Social_Network.domain.model.user.Device;
//import com.GHTK.Social_Network.domain.model.user.Token;
//import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntity;
//import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface DeviceMapperETD {
//    @Mapping(source = "deviceEntity.deviceId", target = "deviceId")
//    @Mapping(source = "userId", target = "userId")
//    Device toDomain(DeviceEntity deviceEntity);
//
//    @Mapping(source = "deviceId", target = "deviceEntity.deviceId")
//    @Mapping(source = "userId", target = "userId")
//    DeviceEntity toEntity(Device device);
//
