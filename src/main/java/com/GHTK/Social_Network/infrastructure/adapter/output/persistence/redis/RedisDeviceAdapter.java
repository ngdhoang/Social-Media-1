package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.RedisDevicePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.DeviceRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisDeviceAdapter implements RedisDevicePort {
    private final RedisTemplate<String, DeviceRedisDto> deviceRedisTemplate;

    @Override
    public DeviceRedisDto findByKey(String key) {
        return deviceRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void createOrUpdate(String key, DeviceRedisDto deviceRedisDto) {
        deviceRedisTemplate.opsForValue().set(key, deviceRedisDto);
    }

    @Override
    public void deleteByKey(String key) {
        deviceRedisTemplate.delete(key);
    }

    @Override
    public String formatKey(String key) {
        return "";
    }

    @Override
    public Boolean existsByKey(String key) {
        return deviceRedisTemplate.hasKey(key);
    }
}
