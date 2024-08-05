package com.GHTK.Social_Network.application.service.Authentication;

import com.GHTK.Social_Network.application.port.input.DevicePortInput;
import com.GHTK.Social_Network.application.port.output.OtpPort;
import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.application.port.output.auth.RedisDevicePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.DeviceRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.OTPRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.VerifyDeviceRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua_parser.Parser;

import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService implements DevicePortInput {

    private final Parser uaParser = new Parser();

    private final DevicePort devicePort;

    private final OtpPort otpPort;

    private final RedisDevicePort redisDevicePort;

    @Override
    public boolean checkDevice(String email,Long userId, String deviceName, String macAddress, String fingerPrint) {
        boolean check = devicePort.existDeviceForUser(userId, deviceName, macAddress, fingerPrint);
        if (!check) {
            // tao otp
            // luu redis
            String otp = otpPort.generateOtp();
            String key = otp + "_" + userId;
            DeviceRedisDto deviceRedisDto = new DeviceRedisDto(deviceName,macAddress,fingerPrint,false);
            saveOtpToRedis(key,deviceRedisDto);
            otpPort.sendOtpEmail(email,otp);
        }
        return check;
    }

    @Override
    public MessageResponse checkOtpLoginNewDevice(String otp, VerifyDeviceRequest verifyDeviceRequest) {
        validateOtp(verifyDeviceRequest.getKey(),verifyDeviceRequest.getDeviceName(),verifyDeviceRequest.getMacAddress(),verifyDeviceRequest.getFingerPrint());

        System.out.println("True");
        return new MessageResponse("Login to new device successful");
    }

//    @Override
//    public MessageResponse checkOtpLoginNewDevice(OTPRequest otp,AuthRequest authRequest) {
//        validateOtp(otp.getOtp(),authRequest.getUserEmail() );
//        return null;
//    }

//    @Override
//    public MessageResponse checkOtpLoginNewDevice(AuthRequest authRequest, int attemptCount, Long timeInterval) {
//        vali
//    }

    public void saveOtpToRedis(String key, DeviceRedisDto deviceRedisDto) {
        redisDevicePort.createOrUpdate(key, deviceRedisDto);
    }

    public void validateOtp(String key,String deviceName, String macAddress, String fingerPrint){
        DeviceRedisDto deviceRedisDto = redisDevicePort.findByKey(key + "_1");
        if(deviceRedisDto == null){
//            redisDevicePort.deleteByKey(key+"*_1");
            throw new CustomException("OTP not found", HttpStatus.BAD_REQUEST);
        }
        System.out.println("True");
        // .... create token
        if (deviceRedisDto.getDeviceName().equals(deviceName)&&deviceRedisDto.getMacAddress().equals(macAddress)&&deviceRedisDto.getFingerPrint().equals(fingerPrint)  ){
            redisDevicePort.deleteByKey(key);
            throw new CustomException("Invalid OTP",HttpStatus.BAD_REQUEST);
        }

        redisDevicePort.deleteByKey(key);
    }






    public static int generateRandomCode() {
        Random random = new Random();
        return 10 + random.nextInt(90); // Tạo số ngẫu nhiên từ 10 đến 99
    }

    // Phương thức tạo 3 mã code
    public static int[] generateThreeCodes() {
        int[] codes = new int[3];
        for (int i = 0; i < 3; i++) {
            codes[i] = generateRandomCode();
        }
        return codes;
    }
    // Phương thức chọn ngẫu nhiên một mã từ 3 mã đã tạo
    public static int selectRandomCode(int[] codes) {
        Random random = new Random();
        int index = random.nextInt(codes.length); // Chọn ngẫu nhiên chỉ số từ 0 đến 2
        return codes[index];
    }
}

