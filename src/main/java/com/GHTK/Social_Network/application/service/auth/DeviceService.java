package com.GHTK.Social_Network.application.service.auth;

import com.GHTK.Social_Network.application.port.input.post.DevicePortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.application.port.output.auth.RedisAuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.Device;
import com.GHTK.Social_Network.domain.model.user.EDeviceType;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DeviceService implements DevicePortInput {
  private final DevicePort devicePort;
  private final RedisAuthPort redisAuthPort;
  private final AuthPort authPort;

  @Override
  public MessageResponse checkDeviceOtp(int otp) {
    if (otp < 0) { // no success new device
      redisAuthPort.deleteAllByTail(RedisAuthPort.DEVICE_TAIL + authPort.getUserAuth().getUserEmail());
      return new MessageResponse("Success delete device");
    }
    String key = otp + RedisAuthPort.DEVICE_TAIL + authPort.getUserAuth().getUserEmail();
    if (redisAuthPort.existsByKey(otp + RedisAuthPort.DEVICE_TAIL + authPort.getUserAuth().getUserEmail())) {
      AuthRedisDto authRedisDto = redisAuthPort.findByKey(key);
      String newKey = authRedisDto.getKey() + "_" + authRedisDto.getFingerprinting() + "_" + authRedisDto.getUserAgent() + RedisAuthPort.DEVICE_CHECK_TAIL;
      RegisterRequest registerRequest = RegisterRequest.builder().userEmail(authPort.getUserAuth().getUserEmail()).build();
      redisAuthPort.createOrUpdate(newKey, AuthRedisDto.builder()
              .registerRequest(registerRequest)
              .build());
      devicePort.saveDevice(
              new Device(
                      authRedisDto.getFingerprinting(),
                      authRedisDto.getUserAgent(),
                      EDeviceType.NORMAL
              ),
              authPort.getUserAuth().getUserId()
      );
      redisAuthPort.deleteByKey(key);
      return new MessageResponse("Success new device");
    }
    throw new CustomException("Otp invalid", HttpStatus.BAD_REQUEST);
  }

  @Override
  public Map<String, Object> generateOtp(int n) {
    List<Integer> numbers = generateRandomNumbers(n);
    int selectedNumber = selectRandomNumber(numbers);

    Map<String, Object> result = new HashMap<>();
    result.put("selectedNumber", selectedNumber);
    result.put("generatedNumbers", numbers);

    return result;
  }

  private List<Integer> generateRandomNumbers(int n) {
    Random random = new Random();
    List<Integer> numbers = new ArrayList<>();

    while (numbers.size() < n) {
      int number = random.nextInt(90) + 10;
      if (!numbers.contains(number)) {
        numbers.add(number);
      }
    }

    return numbers;
  }

  private int selectRandomNumber(List<Integer> numbers) {
    Random random = new Random();
    int index = random.nextInt(numbers.size());
    return numbers.get(index);
  }
}
