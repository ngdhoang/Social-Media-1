package com.GHTK.Social_Network.application.service.Authentication;

import com.GHTK.Social_Network.application.port.input.OtpPortInput;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService implements OtpPortInput {
  private final JavaMailSender mailSender;

  @Value("spring.mail.username")
  private String hostEmail;

  private Random random = new Random();

  @Override
  public String generateOTP() {
    return String.format("%06d", random.nextInt(1000000));
  }

  @Override
  @Async("taskExecutor")
  public CompletableFuture<Void> sendOtpEmail(String email, String otp) {
    log.info("Preparing to send OTP email to: {}", email);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(hostEmail);
    message.setTo(email);
    message.setSubject("Your OTP Code");
    message.setText("Your OTP code is: " + otp);

    try {
      log.info("Attempting to send email...");
      mailSender.send(message);
      log.info("OTP email sent successfully to: {}", email);
        return CompletableFuture.completedFuture(null);
    } catch (MailException e) {
      log.error("Failed to send OTP email", e);
      throw new CustomException("Failed to send OTP email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
