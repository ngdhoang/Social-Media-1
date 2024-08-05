package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class DeviceAdapter implements DevicePort {
    private final TokenRepository tokenRepository;

    @Override
    public boolean existDeviceForUser(Long userId, String deviceName, String macAddress, String fingerPrint) {
        return tokenRepository.existDeviceForUser(userId,deviceName,macAddress,fingerPrint);
    }
//
//    @Override
//    public Token findByUserIdAndAndDefaultDeviceIsTrue(Long userId) {
//        return tokenMapperETD.toDomain(tokenRepository.findByUserIdAndAndDefaultDeviceIsTrue(userId));

}
