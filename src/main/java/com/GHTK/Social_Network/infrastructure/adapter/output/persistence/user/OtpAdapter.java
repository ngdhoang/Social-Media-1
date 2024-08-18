package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.OtpPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpAdapter implements OtpPort {
  private final RedisTemplate<String, AuthRedisDto> authenticationRedisTemplate;
  private final JavaMailSender mailSender;

  @Value("spring.mail.username")
  private String hostEmail;

  @Override
  public void validateOtp(String email, String providedOtp, int maxAttempts, long timeInterval) {
    AuthRedisDto authRedisDto = authenticationRedisTemplate.opsForValue().get(email);
    if (authRedisDto == null) {
      throw new CustomException("OTP not found", HttpStatus.BAD_REQUEST);
    }

    if (authRedisDto.getCount() >= maxAttempts) {
      authenticationRedisTemplate.delete(email);
      throw new CustomException("Maximum OTP attempts exceeded", HttpStatus.TOO_MANY_REQUESTS);
    }

    if (System.currentTimeMillis() > authRedisDto.getCreateAt().getTime() + timeInterval) {
      authenticationRedisTemplate.delete(email);
      throw new CustomException("OTP has expired", HttpStatus.BAD_REQUEST);
    }

    if (!authRedisDto.getOtp().equals(providedOtp)) {
      authRedisDto.setCount(authRedisDto.getCount() + 1);
      authenticationRedisTemplate.opsForValue().set(email, authRedisDto);
      throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
    }

    authenticationRedisTemplate.delete(email);
  }

  @Override
  public void saveOtp(String email, String otp) {
    AuthRedisDto authRedisDto = new AuthRedisDto(List.of(otp), new Date(), 0);
    authenticationRedisTemplate.opsForValue().set(email, authRedisDto);
  }

  @Override
  public String generateOtp() {
    Random random = new Random();
    return String.format("%06d", random.nextInt(1000000));
  }

  @Async
  @Override
  public void sendOtpEmail(String email, String otp) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(hostEmail);
    message.setTo(email);
    message.setSubject("Your OTP Code");
    message.setText("Your OTP code is: " + otp);

    try {
      mailSender.send(message);
    } catch (MailException e) {
      throw new CustomException("Failed to send OTP email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
