package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.RandomStringGeneratorPortInput;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RandomStringGeneratorService implements RandomStringGeneratorPortInput {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  @Override
  public String generateRandomPublicId(int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      int index = RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }
    return sb.toString();
  }
}
