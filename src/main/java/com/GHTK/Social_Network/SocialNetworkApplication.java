package com.GHTK.Social_Network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SocialNetworkApplication {
  public static void main(String[] args) {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));

    SpringApplication.run(SocialNetworkApplication.class, args);
  }

}
