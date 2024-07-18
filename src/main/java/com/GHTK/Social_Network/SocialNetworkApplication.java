package com.GHTK.Social_Network;

import com.GHTK.Social_Network.domain.entity.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SocialNetworkApplication {
  public static void main(String[] args) {
//    User user = new User("BA","Nguyen","ducpham@gmail.com", PasswordEncoder.encode);

    SpringApplication.run(SocialNetworkApplication.class, args);
  }
}
